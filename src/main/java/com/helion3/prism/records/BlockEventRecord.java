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

public class BlockEventRecord implements EventRecord {

    private final String eventName;
    private final Location location;
    private final Optional<String> existingBlockId;
    private final Optional<String> replacementBlockId;
    private final Date date;

    /**
     * Represents an event which occurred to a block, for which there was
     * an empty/air replacement block at this location.
     * 
     * @param eventName Prism parameter name for this event.
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     */
    public BlockEventRecord(String eventName, Location location, @Nullable String existingBlockId) {
        this(eventName, location, existingBlockId, null);
    }

    /**
     * Represents an event which occurred to block, and has a specific
     * replacement block for the same location.
     * 
     * @param eventName Prism parameter name for this event.
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     * @param replacementBlockId Minecraft ID for the replacement block.
     */
    public BlockEventRecord(String eventName, Location location, @Nullable String existingBlockId, @Nullable String replacementBlockId) {
        this(eventName, new Date(), location, existingBlockId, replacementBlockId);
    }
    
    /**
     * Represents any event which has a block as the subject. This constructor 
     * does not auto-generator a timestamp and should only be used for database
     * results.
     * 
     * @private
     * 
     * @param eventName Prism parameter name for this event.
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     * @param replacementBlockId Minecraft ID for the replacement block.
     */
    public BlockEventRecord(String eventName, Date date, Location location, @Nullable String existingBlockId, @Nullable String replacementBlockId) {
        this.eventName = eventName;
        this.location = location;
        this.existingBlockId = Optional.fromNullable(existingBlockId);
        this.replacementBlockId = Optional.fromNullable(replacementBlockId);
        this.date = date;
    }

    /**
     * Returns the parameter name for this {@link EventRecord}.
     * 
     * @return String Name of the event
     */
    @Override
    public String getName() {
        return eventName;
    }

    /**
     * Returns the location for this {@link EventRecord}.
     * 
     * @return Location of the event
     */
    @Override
    public Location getLocation() {
        return location;
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
    
    /**
     * Returns the date/time of this event.
     * 
     * @return Date The date the event occurred
     */
    public Date getDate(){
        return date;
    }
}