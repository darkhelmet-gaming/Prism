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
package com.helion3.prism.records;

import java.util.Date;

import javax.annotation.Nullable;

import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;
import com.helion3.prism.api.records.EventRecord;
import com.helion3.prism.api.records.EventSource;

public class BlockEventRecord extends EventRecord {
    
    private final Optional<String> existingBlockId;
    private final Optional<String> replacementBlockId;

    /**
     * Represents an event which occurred to a block, for which there was
     * an empty/air replacement block at this location.
     * 
     * @param eventName Prism parameter name for this event.
     * @param source {@link EventSource} Source/cause of this event
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     */
    public BlockEventRecord(String eventName, EventSource source, Location location, @Nullable String existingBlockId) {
        this(eventName, source, location, existingBlockId, null);
    }

    /**
     * Represents an event which occurred to block, and has a specific
     * replacement block for the same location.
     * 
     * @param eventName Prism parameter name for this event.
     * @param source {@link EventSource} Source/cause of this event
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     * @param replacementBlockId Minecraft ID for the replacement block.
     */
    public BlockEventRecord(String eventName, EventSource source, Location location, @Nullable String existingBlockId, @Nullable String replacementBlockId) {
        this(eventName, source, new Date(), location, existingBlockId, replacementBlockId);
    }
    
    /**
     * Represents any event which has a block as the subject. This constructor 
     * does not auto-generator a timestamp and should only be used for database
     * results.
     * 
     * @private
     * 
     * @param eventName Prism parameter name for this event.
     * @param source {@link EventSource} Source/cause of this event
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     * @param replacementBlockId Minecraft ID for the replacement block.
     */
    public BlockEventRecord(String eventName, EventSource source, Date date, Location location, @Nullable String existingBlockId, @Nullable String replacementBlockId) {
        super(eventName,source,date,location);
        this.existingBlockId = Optional.fromNullable(existingBlockId);
        this.replacementBlockId = Optional.fromNullable(replacementBlockId);
    }

    /**
     * Returns the existing block affected by this {@link EventRecord}.
     * 
     * @return Optional<String> Minecraft block ID
     */
    public Optional<String> getExistingBlockId() {
        return existingBlockId;
    }
    
    /**
     * Returns the replacement block used for this {@link EventRecord}.
     * 
     * @return Optional<String> Minecraft block ID
     */
    public Optional<String> getReplacementBlockId() {
        return replacementBlockId;
    }
}