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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTransaction;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.entity.living.player.Player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.records.EventSource;
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
 * new PrismRecord().player(player).brokeBlock(blockLoc).save()
 *
 */
public class PrismRecord {
    private String eventName;
    private EventSource source;
    private DataContainer data = new MemoryDataContainer();

    /**
     * Describe the Player responsible for the event this
     * record describes.
     *
     * @param player Player responsible for this event
     * @return PrismRecord
     */
    public PrismRecord player(Player player){
        this.source = new EventSource(player);
        return this;
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
            unsafeData.remove(DataQueries.x);
            unsafeData.remove(DataQueries.y);
            unsafeData.remove(DataQueries.z);
            block.set(DataQueries.UnsafeData, unsafeData);
        }

        return block;
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
     * @return
     */
    public PrismRecord joined() {
        this.eventName = "player-join";
        return this;
    }

    /**
     * Describes a player quit.
     * @return
     */
    public PrismRecord quit() {
        this.eventName = "player-quit";
        return this;
    }

    /**
     * Build the final event record and send it to the queue.
     */
    public void save(){
        // Validation
        if (source == null) {
            throw new IllegalArgumentException("Event record can not be created - invalid source.");
        }
        else if (eventName == null) {
            throw new IllegalArgumentException("Event record can not be created - invalid event name.");
        }

        data.set(DataQueries.EventName, eventName);
        data.set(DataQueries.Created, new Date());

        // Source
        DataQuery sourceKey = source.isPlayer() ? DataQueries.Player : DataQueries.Source;
        data.set(sourceKey, source.getSourceIdentifier());

        // Queue the finished record for saving
        RecordingQueue.add(data);
    }
}