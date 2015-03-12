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

import org.spongepowered.api.entity.player.Player;

/**
 * A simple wrapper for describing the source, or cause, of
 * an event which we're going to record.
 * 
 * Players will have their UUID stored, while every other cause
 * will simply be named.
 * 
 */
final public class EventSource {
    
    private final String source;
    private final boolean isPlayer;
    
    /**
     * A named event source which isn't a player.
     * @param source String Name of source (cause) of event
     */
    public EventSource(String source){
        this(source,false);
    }
    
    /**
     * A known Player is the source (cause) of this event.
     * 
     * @param player Player
     */
    public EventSource(Player player){
        this(player.getUniqueId().toString(),true);
    }
    
    /**
     * Parent constructor for creating the proper type
     * of event source.
     * 
     * @private
     * @param source
     * @param isPlayer
     */
    public EventSource(String source, boolean isPlayer){
        this.source = source;
        this.isPlayer = isPlayer;
    }
    
    /**
     * Returns a string naming the source of an event
     * 
     * @return String Name of event source
     */
    public String getSourceIdentifier(){
        return source;
    }
    
    /**
     * Whether or not the named source is a real player
     * 
     * @return Boolean If a real player
     */
    public boolean isPlayer(){
        return isPlayer;
    }
}