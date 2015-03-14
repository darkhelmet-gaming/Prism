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

import javax.annotation.Nullable;

import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;

/**
 * Represents a single record of an event. Contains 
 * necessary information about the event including the 
 * location, subject, time, causation, etc.
 * 
 */
abstract public class EventRecord {
    
    // Required values
    protected final String eventName;
    protected final Date date;
    protected final EventSource source;
    
    // Optional
    protected final Optional<Location> location;
    
    /**
     * 
     * @param eventName Parameter name of the event
     * @param source Source/cause of the event
     * @param location Location the event occurred
     * @param date Time at which the event occurred
     */
    public EventRecord(String eventName, EventSource source, Date date, @Nullable Location location ){
        this.eventName = eventName;
        this.source = source;
        this.date = date;
        this.location = Optional.fromNullable(location);
    }

    /**
     * Returns the parameter name for this {@link EventRecord}.
     * 
     * @return String name of the event
     */
    public String getEventName(){
        return eventName;
    }

    /**
     * Returns the location for this {@link EventRecord}.
     * 
     * @return Location of the event
     */
    public Optional<Location> getLocation(){
        return location;
    }
    
    /**
     * Returns the date/time of this event.
     * 
     * @return Date The date the event occurred
     */
    public Date getDate(){
        return date;
    }
    
    /**
     * Returns the source of this specific event.
     * 
     * @return EventSource Source of this event
     */
    public EventSource getsSource(){
        return source;
    }
    
    /**
     * Return a human-readable display name/summary
     * of the event subject, i.e. "dirt", "creeper"
     * or "diamond pickaxe", etc.
     * 
     * @return Human-readable summary
     */
    public String getSubjectDisplayName(){
        return "";
    }
}