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

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.player.Player;

import static com.google.common.base.Preconditions.checkNotNull;

import com.helion3.prism.queues.RecordingQueue;
import com.helion3.prism.records.EventRecord;
import com.helion3.prism.records.EventSource;

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
    private Location location;
    private EventSource source;
    private Map<String,String> data = new HashMap<String,String>();

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
     * Describes a single block break at a given Location.
     *
     * @param block Block broken.
     * @return PrismRecord
     */
    public PrismRecord brokeBlock(Location block){
        checkNotNull(block);
        this.eventName = "block-break";
        data.put("existingBlockId", block.getBlockType().getId());
        location = block;
        return this;
    }

    /**
     * Describes a single block place at a given Location.
     *
     * @param block Block placed.
     * @return PrismRecord
     */
    public PrismRecord placedBlock(Location block){
        checkNotNull(block);
        this.eventName = "block-place";
        data.put("existingBlockId", block.getBlockType().getId());
        location = block;
        return this;
    }

    /**
     * Describes which block was replaced by a block action.
     * @param snapshot BlockSnapshot Snapshot of the former block
     * @return PrismRecord
     */
    public PrismRecord replacing(BlockSnapshot snapshot) {
        if (snapshot != null) {
            data.put("replacementBlockId", snapshot.getState().getType().getId());
        }
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

        EventRecord record = new EventRecord(eventName, source, data, location);

        // Queue the finished record for saving
        RecordingQueue.add(record);
    }
}