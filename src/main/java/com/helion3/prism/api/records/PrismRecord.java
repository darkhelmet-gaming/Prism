/**
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
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.World;

import static com.google.common.base.Preconditions.checkNotNull;

import com.helion3.prism.Prism;
import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.utils.DataQueries;
import com.helion3.prism.utils.DataUtils;

/**
 * An easy-to-understand factory class for Prism {@link EventRecord}s.
 *
 * By chaining methods together, you can build a record with
 * natural-language style syntax.
 *
 * For example:
 *
 * new PrismRecord().player(player).brokeBlock(transaction).save()
 *
 */
public class PrismRecord {
    private String eventName;
    private Object cause;
    private DataContainer data = new MemoryDataContainer();

    /**
     * Set a cause based on a Cause chain.
     *
     * @param cause Cause of event.
     * @return PrismRecord
     */
    public PrismRecord causedBy(Cause cause) {
        // @todo debug
        for (Object obj : cause.all()) {
            Prism.getLogger().debug("Cause: " + obj.getClass().getName());
        }

        // Player?
        Optional<Player> player = cause.first(Player.class);
        if (player.isPresent()) {
            this.cause = player.get();
        }

        // World?
        Optional<World> world = cause.first(World.class);
        if (world.isPresent()) {
            this.cause = world.get();
        }

        // Default to something!
        if (this.cause == null) {
            this.cause = cause.all().get(0).getClass().getSimpleName();
        }

        return this;
    }

    /**
     * Set the Player responsible for this event.
     *
     * @param player Player responsible for this event
     * @return PrismRecord
     */
    public PrismRecord player(Player player){
        this.cause = player;
        return this;
    }

    /**
     * Sets the target entity for this event.
     *
     * @param entity Entity target for this event
     * @return PrismRecord
     */
    public PrismRecord entity(Entity entity){
        data.set(DataQueries.Entity, entity.toContainer());
        return this;
    }

    /**
     * Describes a single block break at a given Location.
     *
     * @param transaction Block broken.
     * @return PrismRecord
     */
    public PrismRecord brokeBlock(BlockTransaction transaction){
        this.eventName = "block-break";
        writeBlockTransaction(transaction);
        return this;
    }

    /**
     * Describes a single block break at a given Location.
     *
     * @param transaction Block broken.
     * @return PrismRecord
     */
    public PrismRecord decayedBlock(BlockTransaction transaction){
        this.eventName = "block-decay";
        writeBlockTransaction(transaction);
        return this;
    }

    /**
     * Describes a single block break at a given Location.
     *
     * @param transaction Block broken.
     * @return PrismRecord
     */
    public PrismRecord grewBlock(BlockTransaction transaction){
        this.eventName = "block-grow";
        writeBlockTransaction(transaction);
        return this;
    }

    /**
     * Describes a single block place at a given Location.
     *
     * @param transaction Block placed.
     * @return PrismRecord
     */
    public PrismRecord placedBlock(BlockTransaction transaction){
        this.eventName = "block-place";
        writeBlockTransaction(transaction);
        return this;
    }

    /**
     * Describes a player join.
     *
     * @return PrismRecord
     */
    public PrismRecord joined() {
        this.eventName = "player-join";
        return this;
    }

    /**
     * Describes a player quit.
     *
     * @return PrismRecord
     */
    public PrismRecord quit() {
        this.eventName = "player-quit";
        return this;
    }

    /**
     * Describes an entity death.
     *
     * @return PrismRecord
     */
    public PrismRecord died() {
        this.eventName = "entity-death";
        return this;
    }

    /**
     * Returns whether or not this record is valid and ready to be saved.
     *
     * @return Boolean
     */
    public boolean isValid() {
        return (cause != null && eventName != null);
    }

    /**
     * Build the final event record and send it to the queue.
     */
    public void save(){
        // Validation
        if (cause == null) {
            throw new IllegalArgumentException("Event record can not be created - invalid cause.");
        }
        else if (eventName == null) {
            throw new IllegalArgumentException("Event record can not be created - invalid event name.");
        }

        data.set(DataQueries.EventName, eventName);
        data.set(DataQueries.Created, new Date());

        // Cause
        DataQuery causeKey = (cause instanceof Player) ? DataQueries.Player : DataQueries.Cause;

        String causeIdentifier = "environment";
        if (cause instanceof Player) {
            causeIdentifier = ((Player) cause).getUniqueId().toString();
        }

        data.set(causeKey, causeIdentifier);

        // Queue the finished record for saving
        RecordingQueue.add(data);
    }

    /**
     * Helper method for writing block transaction data, using only
     * the final replacement value. We must alter the data structure
     * slightly to avoid duplication, decoupling location from blocks, etc.
     *
     * @param transaction BlockTransaction representing a block change in the world.
     */
    private void writeBlockTransaction(BlockTransaction transaction) {
        checkNotNull(transaction);

        Prism.getLogger().debug(DataUtils.jsonFromDataView(transaction.getOriginal().toContainer()).toString());

        // Location
        DataContainer location = transaction.getOriginal().getLocation().get().toContainer();
        location.remove(DataQueries.BlockType);
        location.remove(DataQueries.WorldName);
        data.set(DataQueries.Location, location);

        // Storing the state only, so we don't also get location
        data.set(DataQueries.OriginalBlock, formatBlockDataContainer(transaction.getOriginal()));
        data.set(DataQueries.ReplacementBlock, formatBlockDataContainer(transaction.getFinalReplacement()));
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