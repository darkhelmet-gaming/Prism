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
package com.helion3.prism.events.handlers;

import org.spongepowered.api.world.Location;

import com.helion3.prism.api.events.Event;

public class BlockEvent implements Event {

    private final String eventName;
    private final Location location;
    private final String existingBlockId;

    /**
     * Represents an event which occurs to a specific block. Defaults the
     * replacement block to an empty string.
     * 
     * @param eventName Prism parameter name for this event.
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     */
    public BlockEvent(String eventName, Location location, String existingBlockId) {
        this(eventName, location, existingBlockId, "");
    }

    /**
     * Represents an event which occurs to a specific block.
     * 
     * @param eventName Prism parameter name for this event.
     * @param location Location of the block affected.
     * @param existingBlockId Minecraft ID for the existing block.
     * @param replacementBlockId Minecraft ID for the replacement block.
     */
    public BlockEvent(String eventName, Location location, String existingBlockId, String replacementBlockId) {
        this.eventName = eventName;
        this.location = location;
        this.existingBlockId = existingBlockId;
    }

    /**
     * Returns the parameter name for this {@link Event}.
     * 
     * @return String name of the event
     */
    @Override
    public String getName() {
        return eventName;
    }

    /**
     * Returns the location for this {@link Event}.
     * 
     * @return Location of the event
     */
    @Override
    public Location getLocation() {
        return location;
    }

    /**
     * Returns the existing block affected by this {@link Event}.
     * 
     * @return Minecraft block ID
     */
    public String getExistingBlockId() {
        return existingBlockId;
    }
}