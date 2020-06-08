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

import com.google.common.base.Preconditions;
import com.helion3.prism.Prism;
import com.helion3.prism.api.data.PrismEvent;
import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DataUtil;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.entity.damage.source.EntityDamageSource;
import org.spongepowered.api.event.cause.entity.damage.source.IndirectEntityDamageSource;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.util.Date;
import java.util.Optional;

/**
 * An easy-to-understand factory class for Prism records,
 * By chaining methods together, you can build a record.
 *
 * <pre><b>Example:</b>{@code
 * PrismRecord.create()
 *          .player(player)
 *          .event(PrismEvents.PLAYER_JOIN)
 *          .location(player.getLocation())
 *          .buildAndSave();
 * }</pre>
 */
public class PrismRecord {

    private final String event;
    private final Object source;
    private final DataContainer dataContainer;

    /**
     * A final, save-ready record.
     *
     * @param event         Event Id
     * @param source        Source
     * @param dataContainer DataContainer
     */
    private PrismRecord(String event, Object source, DataContainer dataContainer) {
        this.event = event;
        this.source = source;
        this.dataContainer = dataContainer;
    }

    /**
     * Save the current record.
     */
    public void save() {
        DataUtil.writeToDataView(getDataContainer(), DataQueries.Created, new Date());
        DataUtil.writeToDataView(getDataContainer(), DataQueries.EventName, getEvent());

        DataQuery causeKey = DataQueries.Cause;
        String causeValue = "environment";
        if (getSource() instanceof Player) {
            causeKey = DataQueries.Player;
            causeValue = ((Player) getSource()).getUniqueId().toString();
        } else if (getSource() instanceof Entity) {
            causeValue = ((Entity) getSource()).getType().getName();
        }

        DataUtil.writeToDataView(getDataContainer(), causeKey, causeValue);

        // Source filtered?
        if (!Prism.getInstance().getFilterList().allowsSource(getSource())) {
            return;
        }

        // Original block filtered?
        Optional<BlockType> originalBlockType = getDataContainer().getObject(DataQueries.OriginalBlock.then(DataQueries.BlockState).then(DataQueries.BlockType), BlockType.class);
        if (originalBlockType.map(Prism.getInstance().getFilterList()::allows).orElse(false)) {
            return;
        }

        // Replacement block filtered?
        Optional<BlockType> replacementBlockType = getDataContainer().getObject(DataQueries.ReplacementBlock.then(DataQueries.BlockState).then(DataQueries.BlockType), BlockType.class);
        if (replacementBlockType.map(Prism.getInstance().getFilterList()::allows).orElse(false)) {
            return;
        }

        // Queue the finished record for saving
        RecordingQueue.add(this);
    }

    /**
     * Create a new source builder.
     *
     * @return The created SourceBuilder instance
     */
    public static PrismRecord.SourceBuilder create() {
        return new PrismRecord.SourceBuilder();
    }

    public String getEvent() {
        return event;
    }

    public Object getSource() {
        return source;
    }

    public DataContainer getDataContainer() {
        return dataContainer;
    }

    /**
     * Build record event/action details.
     */
    public static class EventBuilder {

        private final Object source;
        private String event;
        private DataContainer dataContainer;

        protected EventBuilder(Object source) {
            this.source = source;
            this.event = "unknown";
            this.dataContainer = DataContainer.createNew();
        }

        /**
         * Helper method for writing original BlockSnapshot container data.
         *
         * @param block The original BlockSnapshot to write
         */
        public EventBuilder blockOriginal(BlockSnapshot block) {
            Preconditions.checkNotNull(block);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.OriginalBlock, formatBlockDataContainer(block));
            return this;
        }

        /**
         * Helper method for writing replacement BlockSnapshot container data.
         *
         * @param block The replacement BlockSnapshot to write
         */
        public EventBuilder blockReplacement(BlockSnapshot block) {
            Preconditions.checkNotNull(block);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.ReplacementBlock, formatBlockDataContainer(block));
            return this;
        }

        /**
         * Helper method for writing container name.
         *
         * @param container The container name to write
         */
        public EventBuilder container(String container) {
            Preconditions.checkNotNull(container);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.Container, container);
            return this;
        }

        /**
         * Helper method for writing Entity container data.
         *
         * @param entity The Entity to write
         */
        public EventBuilder entity(Entity entity) {
            Preconditions.checkNotNull(entity);

            DataContainer entityData = entity.toContainer();

            Optional<DataView> position = entityData.getView(DataQueries.Position);
            if (position.isPresent()) {
                position.get().set(DataQueries.WorldUuid, entityData.get(DataQueries.WorldUuid).get());
                DataUtil.writeToDataView(getDataContainer(), DataQueries.Location, position.get());

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

            DataUtil.writeToDataView(getDataContainer(), DataQueries.Entity, entityData);
            DataUtil.writeToDataView(getDataContainer(), DataQueries.Id, entity.getType().getId());
            DataUtil.writeToDataView(getDataContainer(), DataQueries.Target, entity.get(Keys.DISPLAY_NAME).map(Text::toPlain).orElse(entity.getType().getName()));
            return this;
        }

        /**
         * Helper method for writing Item container data.
         *
         * @param item The Item to write
         */
        public EventBuilder item(Item item) {
            Preconditions.checkNotNull(item);
            Preconditions.checkArgument(item.item().exists());

            itemStack(item.item().get());
            location(item.getLocation());
            return this;
        }

        /**
         * Helper method for writing ItemStack container data.
         *
         * @param itemStack The ItemStack to write
         */
        public EventBuilder itemStack(ItemStack itemStack) {
            Preconditions.checkNotNull(itemStack);
            return itemStack(itemStack, itemStack.getQuantity());
        }

        /**
         * Helper method for writing ItemStack container data.
         *
         * @param itemStack The ItemStack to write
         * @param quantity  The quantity to write
         */
        public EventBuilder itemStack(ItemStack itemStack, int quantity) {
            Preconditions.checkNotNull(itemStack);
            Preconditions.checkArgument(itemStack.getType() != ItemTypes.NONE);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.Target, itemStack.getType().getId());
            DataUtil.writeToDataView(getDataContainer(), DataQueries.Quantity, quantity);
            return this;
        }

        /**
         * Helper method for writing ItemStackSnapshot container data.
         *
         * @param itemStack The ItemStackSnapshot to write
         */
        public EventBuilder itemStack(ItemStackSnapshot itemStack) {
            Preconditions.checkNotNull(itemStack);
            return itemStack(itemStack, itemStack.getQuantity());
        }

        /**
         * Helper method for writing ItemStackSnapshot container data.
         *
         * @param itemStack The ItemStackSnapshot to write
         * @param quantity  The quantity to write
         */
        public EventBuilder itemStack(ItemStackSnapshot itemStack, int quantity) {
            Preconditions.checkNotNull(itemStack);
            Preconditions.checkArgument(itemStack.getType() != ItemTypes.NONE);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.Target, itemStack.getType().getId());
            DataUtil.writeToDataView(getDataContainer(), DataQueries.Quantity, quantity);
            return this;
        }

        /**
         * Helper method for writing Location container data.
         *
         * @param location The location to write
         */
        public EventBuilder location(Location<World> location) {
            Preconditions.checkNotNull(location);

            DataContainer container = location.toContainer();
            container.remove(DataQueries.BlockType);
            container.remove(DataQueries.ContentVersion);
            container.remove(DataQueries.WorldName);
            DataUtil.writeToDataView(getDataContainer(), DataQueries.Location, location);
            return this;
        }

        /**
         * Helper method for writing target container data.
         *
         * @param target The target to write
         */
        public EventBuilder target(String target) {
            Preconditions.checkNotNull(target);

            DataUtil.writeToDataView(getDataContainer(), DataQueries.Target, target);
            return this;
        }

        /**
         * Removes unnecessary/duplicate data from a BlockSnapshot's DataContainer.
         *
         * @param block {@link BlockSnapshot BlockSnapshot}
         * @return Formatted {@link DataContainer DataContainer}
         */
        private DataContainer formatBlockDataContainer(BlockSnapshot block) {
            Preconditions.checkNotNull(block);
            Preconditions.checkNotNull(block.getState(), "Missing BlockState: " + block.toString());

            DataContainer blockData = block.toContainer();
            blockData.remove(DataQueries.Position);
            blockData.remove(DataQueries.WorldUuid);

            DataView unsafeData = blockData.getObject(DataQueries.UnsafeData, DataView.class).orElse(null);
            if (unsafeData != null) {
                unsafeData.remove(DataQueries.X);
                unsafeData.remove(DataQueries.Y);
                unsafeData.remove(DataQueries.Z);
                blockData.set(DataQueries.UnsafeData, unsafeData);
            }

            return blockData;
        }

        /**
         * Creates a new {@link PrismRecord}.
         *
         * @return A new prism record
         */
        public PrismRecord build() {
            Preconditions.checkState(Sponge.getRegistry().getType(PrismEvent.class, getEvent()).isPresent(), getEvent() + " is not registered");
            return new PrismRecord(getEvent(), getSource(), getDataContainer());
        }

        /**
         * Creates a new {@link PrismRecord} and immediately saves it.
         */
        public void buildAndSave() {
            build().save();
        }

        private Object getSource() {
            return source;
        }

        private String getEvent() {
            return event;
        }

        public EventBuilder event(PrismEvent prismEvent) {
            return event(prismEvent.getId());
        }

        public EventBuilder event(String event) {
            this.event = event;
            return this;
        }

        private DataContainer getDataContainer() {
            return dataContainer;
        }

        public EventBuilder dataContainer(DataContainer dataContainer) {
            this.dataContainer = dataContainer;
            return this;
        }
    }

    /**
     * Build record with event source.
     */
    public static class SourceBuilder {

        /**
         * Set a cause based on a Cause chain.
         *
         * @param cause Cause of event.
         * @return The created EventBuilder instance
         */
        public PrismRecord.EventBuilder source(Cause cause) {
            Player player = cause.first(Player.class).orElse(null);
            if (player != null) {
                return new PrismRecord.EventBuilder(player);
            }

            EntityDamageSource attacker = cause.first(EntityDamageSource.class).orElse(null);
            if (attacker != null) {
                return new PrismRecord.EventBuilder(attacker);
            }

            IndirectEntityDamageSource indirectAttacker = cause.first(IndirectEntityDamageSource.class).orElse(null);
            if (indirectAttacker != null) {
                return new PrismRecord.EventBuilder(indirectAttacker);
            }

            if (!cause.all().isEmpty()) {
                return new PrismRecord.EventBuilder(cause.all().get(0));
            }

            return new PrismRecord.EventBuilder(null);
        }

        /**
         * Set the Player responsible for this event.
         *
         * @param player Player responsible for this event
         * @return The created EventBuilder instance
         */
        public PrismRecord.EventBuilder player(Player player) {
            return new PrismRecord.EventBuilder(player);
        }

        /**
         * Set the source non-Entity player responsible for this event.
         *
         * @param entity Entity responsible for this event
         * @return The created EventBuilder instance
         */
        public PrismRecord.EventBuilder entity(Entity entity) {
            return new PrismRecord.EventBuilder(entity);
        }
    }
}