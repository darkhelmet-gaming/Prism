/*
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2015 Helion3 http://helion3.com/
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.helion3.prism.api.records;

import java.util.Date;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;

import static com.google.common.base.Preconditions.checkNotNull;

import com.helion3.prism.Prism;
import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.util.DataQueries;

/**
 * An easy-to-understand factory class for Prism records.
 *
 * By chaining methods together, you can build a record with
 * natural-language style syntax.
 *
 * For example:
 *
 * PrismRecord.create().source(player).broke(transaction).save();
 *
 */
public class PrismRecord {
    private final PrismRecordSourceBuilder source;
    private final PrismRecordEventBuilder event;

    /**
     * A final, save-ready record.
     * @param source Result source builder.
     * @param event Result event builder.
     */
    private PrismRecord(PrismRecordSourceBuilder source, PrismRecordEventBuilder event) {
        this.source = source;
        this.event = event;
    }

    /**
     * Save the current record.
     */
    public void save() {
        event.getData().set(DataQueries.EventName, event.getEventName());
        event.getData().set(DataQueries.Created, new Date());

        // Cause
        DataQuery causeKey = (source.getSource() instanceof Player) ? DataQueries.Player : DataQueries.Cause;

        String causeIdentifier = "environment";
        if (source.getSource() instanceof Player) {
            causeIdentifier = ((Player) source.getSource()).getUniqueId().toString();
        }
        else if(source.getSource() instanceof Entity) {
            causeIdentifier = ((Entity) source.getSource()).getType().getName();
        }

        event.getData().set(causeKey, causeIdentifier);

        // Source filtered?
        if (!Prism.getFilterList().allowsSource(source.getSource())) {
            return;
        }

        // Original block filtered?
        Optional<Object> optionalOriginalBlock = event.getData().get(DataQueries.OriginalBlock.then(DataQueries.BlockState).then(DataQueries.BlockType));
        if (optionalOriginalBlock.isPresent() && !Prism.getFilterList().allowsBlock((String) optionalOriginalBlock.get())) {
            return;
        }

        // Replacement block filtered?
        Optional<Object> optionalReplacementBlock = event.getData().get(DataQueries.ReplacementBlock.then(DataQueries.BlockState).then(DataQueries.BlockType));
        if (optionalReplacementBlock.isPresent() && !Prism.getFilterList().allowsBlock((String) optionalReplacementBlock.get())) {
            return;
        }

        // Queue the finished record for saving
        RecordingQueue.add(event.getData());
    }

    /**
     * Build record with event source.
     */
    public static class PrismRecordSourceBuilder {
        private final Object source;

        private PrismRecordSourceBuilder(Object source) {
            this.source = source;
        }

        public Object getSource() {
            return source;
        }
    }

    /**
     * Build record event/action details.
     */
    public static class PrismRecordEventBuilder {
        protected final PrismRecordSourceBuilder source;
        protected String eventName;
        protected DataContainer data = new MemoryDataContainer();

        private PrismRecordEventBuilder(PrismRecordSourceBuilder source) {
            this.source = source;
        }

        /**
         * Get data.
         * @return DataContainer Data
         */
        public DataContainer getData() {
            return data;
        }

        /**
         * Get the event name.
         *
         * @return String Event name.
         */
        public String getEventName() {
            return eventName;
        }

        /**
         * Describes a single block break at a given Location.
         *
         * @param transaction Block broken.
         * @return PrismRecord
         */
        public PrismRecord brokeBlock(Transaction<BlockSnapshot> transaction) {
            this.eventName = "break";
            writeBlockTransaction(transaction);
            return new PrismRecord(source, this);
        }

        /**
         * Describes a single block break at a given Location.
         *
         * @param transaction Block broken.
         * @return PrismRecord
         */
        public PrismRecord decayedBlock(Transaction<BlockSnapshot> transaction){
            this.eventName = "decay";
            writeBlockTransaction(transaction);
            return new PrismRecord(source, this);
        }

        /**
         * Describes a single item entity drop.
         *
         * @param entity Item Entity dropped.
         * @return PrismRecord
         */
        public PrismRecord dropped(Entity entity) {
            this.eventName = "dropped";
            writeItem((Item) entity);
            return new PrismRecord(source, this);
        }

        /**
         * Describes a single block break at a given Location.
         *
         * @param transaction Block broken.
         * @return PrismRecord
         */
        public PrismRecord grewBlock(Transaction<BlockSnapshot> transaction){
            this.eventName = "grow";
            writeBlockTransaction(transaction);
            return new PrismRecord(source, this);
        }

        /**
         * Describes a single item entity pickup.
         *
         * @param entity Item Entity picked up.
         * @return PrismRecord
         */
        public PrismRecord pickedUp(Entity entity) {
            this.eventName = "picked up";
            writeItem((Item) entity);
            return new PrismRecord(source, this);
        }

        /**
         * Describes a single block place at a given Location.
         *
         * @param transaction Block placed.
         * @return PrismRecord
         */
        public PrismRecord placedBlock(Transaction<BlockSnapshot> transaction){
            this.eventName = "place";
            writeBlockTransaction(transaction);
            return new PrismRecord(source, this);
        }

        /**
         * Describes an entity death.
         *
         * @param entity Living entity.
         * @return PrismRecord
         */
        public PrismRecord killed(Living entity){
            this.eventName = "death";
            writeEntity(entity);
            return new PrismRecord(source, this);
        }

        /**
         * Helper method for writing block transaction data, using only
         * the final replacement value. We must alter the data structure
         * slightly to avoid duplication, decoupling location from blocks, etc.
         *
         * @param transaction BlockTransaction representing a block change in the world.
         */
        private void writeBlockTransaction(Transaction<BlockSnapshot> transaction) {
            checkNotNull(transaction);

            // Location
            DataContainer location = transaction.getOriginal().getLocation().get().toContainer();
            location.remove(DataQueries.BlockType);
            location.remove(DataQueries.WorldName);
            location.remove(DataQueries.ContentVersion);
            data.set(DataQueries.Location, location);

            // Storing the state only, so we don't also get location
            data.set(DataQueries.OriginalBlock, formatBlockDataContainer(transaction.getOriginal()));
            data.set(DataQueries.ReplacementBlock, formatBlockDataContainer(transaction.getFinal()));

            // Use the transaction data directly, so we never worry about different data formats
            if (this.eventName.equals("place")) {
                data.set(DataQueries.Target, transaction.getFinal().getState().getType().getId().replace("_", " "));
            } else {
                data.set(DataQueries.Target, transaction.getOriginal().getState().getType().getId().replace("_", " "));
            }
        }

        /**
         * Helper method for formatting entity container data.
         * @param entity
         */
        private void writeEntity(Entity entity) {
            checkNotNull(entity);

            DataContainer entityData = entity.toContainer();

            Optional<DataView> position = entityData.getView(DataQueries.Position);
            if (position.isPresent()) {
                position.get().set(DataQueries.WorldUuid, entityData.get(DataQueries.WorldUuid).get());
                data.set(DataQueries.Location, position.get());

                entityData.remove(DataQueries.Position);
                entityData.remove(DataQueries.WorldUuid);
            }

            Optional<DataView> optionalUnsafeData = entityData.getView(DataQueries.UnsafeData);
            if (optionalUnsafeData.isPresent()) {
                DataView unsafeData = optionalUnsafeData.get();
                unsafeData.remove(DataQueries.Rotation);
                unsafeData.remove(DataQueries.Pos);
                entityData.set(DataQueries.UnsafeData, unsafeData);
            }

            data.set(DataQueries.Target, entity.getType().getId().replace("_", " "));
            data.set(DataQueries.Entity, entityData);
        }

        /**
         * Helper method for formatting item container data.
         *
         * @param item
         */
        private void writeItem(Item item) {
            checkNotNull(item);

            DataContainer itemData = item.toContainer();

            // Because item actions are not currently actionable, copy only what we need
            Optional<DataView> position = itemData.getView(DataQueries.Position);
            if (position.isPresent()) {
                position.get().set(DataQueries.WorldUuid, itemData.get(DataQueries.WorldUuid).get());
                data.set(DataQueries.Location, position.get());
            }

            String itemId = "";
            int itemQty = 1;
            Optional<DataView> optionalItem = itemData.getView(DataQueries.UnsafeData.then("Item"));
            if (optionalItem.isPresent()) {
                itemId = optionalItem.get().getString(DataQuery.of("id")).orElse("item");
                itemQty = optionalItem.get().getInt(DataQuery.of("Count")).orElse(1);
            }

            data.set(DataQueries.Target, itemId);
            data.set(DataQueries.Quantity, itemQty);
        }

        /**
         * Removes unnecessary/duplicate data from a BlockSnapshot's DataContainer.
         *
         * @param blockSnapshot Block Snapshot.
         * @return DataContainer Formatted Data Container.
         */
        private DataContainer formatBlockDataContainer(BlockSnapshot blockSnapshot) {
            DataContainer block = blockSnapshot.toContainer();
            block.remove(DataQueries.WorldUuid);
            block.remove(DataQueries.Position);

            Optional<Object> optionalUnsafeData = block.get(DataQueries.UnsafeData);
            if (optionalUnsafeData.isPresent()) {
                DataView unsafeData = (DataView) optionalUnsafeData.get();
                unsafeData.remove(DataQueries.X);
                unsafeData.remove(DataQueries.Y);
                unsafeData.remove(DataQueries.Z);
                block.set(DataQueries.UnsafeData, unsafeData);
            }

            return block;
        }
    }

    /**
     * Builder for player-only events.
     */
    public static final class PrismPlayerRecordEventBuilder extends PrismRecordEventBuilder {
        public PrismPlayerRecordEventBuilder(PrismRecordSourceBuilder source) {
            super(source);
        }

        /**
         * Describes a player quit.
         *
         * @return PrismRecordCompleted
         */
        public PrismRecord quit() {
            this.eventName = "quit";
            writePlayerLocation((Player) source.getSource());
            return new PrismRecord(source, this);
        }

        /**
         * Describes a player join.
         *
         * @return PrismRecordCompleted
         */
        public PrismRecord joined() {
            this.eventName = "join";
            writePlayerLocation((Player) source.getSource());
            return new PrismRecord(source, this);
        }

        /**
         * Helper method for formatting player container data.
         *
         * @param player
         */
        private void writePlayerLocation(Player player) {
            checkNotNull(player);

            data.set(DataQueries.Target, player.getName());
            data.set(DataQueries.Location, player.getLocation().toContainer());
        }
    }

    /**
     * Root builder for a new prism record.
     */
    public static final class PrismRecordBuilder {
        /**
         * Set a cause based on a Cause chain.
         *
         * @param cause Cause of event.
         * @return PrismRecord
         */
        public PrismRecordEventBuilder source(Cause cause) {
            Object source = null;

            // Player?
            Optional<Player> player = cause.first(Player.class);
            if (player.isPresent()) {
                source = player.get();
            }

            // Attacker?
            Optional<EntityDamageSource> attacker = cause.first(EntityDamageSource.class);
            if (attacker.isPresent()) {
                source = attacker.get().getSource();
            }

            // Indirect attacker?
            Optional<IndirectEntityDamageSource> indirectAttacker = cause.first(IndirectEntityDamageSource.class);
            if (indirectAttacker.isPresent()) {
                source = indirectAttacker.get().getIndirectSource();
            }

            // Default to something!
            if (source == null) {
                source = cause.all().get(0);
            }

            return new PrismRecordEventBuilder(new PrismRecordSourceBuilder(source));
        }

        /**
         * Set the Player responsible for this event.
         *
         * @param player Player responsible for this event
         * @return PrismRecord
         */
        public PrismPlayerRecordEventBuilder player(Player player) {
            return new PrismPlayerRecordEventBuilder(new PrismRecordSourceBuilder(player));
        }

        /**
         * Set the source non-Entity player responsible for this event.
         *
         * @param entity Entity responsible for this event
         * @return PrismRecord
         */
        public PrismRecordEventBuilder entity(Entity entity) {
            return new PrismRecordEventBuilder(new PrismRecordSourceBuilder(entity));
        }
    }

    /**
     * Create a new record builder.
     * @return PrismRecordBuilder Result builder.
     */
    public static PrismRecordBuilder create() {
        return new PrismRecordBuilder();
    }
}