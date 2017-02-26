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
package com.helion3.prism.api.filters;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.entity.living.player.Player;

public class FilterList {
    private final List<String> blocks = new ArrayList<>();
    private final List<String> players = new ArrayList<>();
    private final List<Class<?>> sources = new ArrayList<>();
    private final FilterMode mode;

    public FilterList(FilterMode mode) {
        this.mode = mode;
    }

    /**
     * Adds a specific source class to the list.
     *
     * @param sourceType The class to add
     */
    public void addSource(Class<?> sourceType) {
        sources.add(sourceType);
    }

    /**
     * Check if this list will allow a given source.
     *
     * @param object The object source to check
     * @return If the list allows this source
     */
    public boolean allowsSource(Object object) {
        boolean contains = false;

        for (Class<?> c : sources) {
            if (c.isInstance(object)) {
                contains = true;
                break;
            }
        }

        return mode.equals(FilterMode.BLACKLIST) ? !contains : contains;
    }

    /**
     * Add a block type to the list.
     *
     * @param block The block type to add
     */
    public void add(BlockType block) {
        addBlock(block.getId());
    }

    /**
     * Add a block type string to the list.
     *
     * @param blockType The block type represented as a string to add
     */
    public void addBlock(String blockType) {
        blocks.add(blockType);
    }

    /**
     * Add a player uuid string to the list.
     *
     * @param uuid The player's UUID represented as a string
     */
    public void addPlayer(String uuid) {
        players.add(uuid);
    }

    /**
     * Get if the list contains a given {@link BlockType}.
     *
     * @param blockType The block type to check
     * @return boolean If the list contains block type
     */
    public boolean allows(BlockType blockType) {
        return allowsBlock(blockType.toString());
    }

    /**
     * Get if list contains a player's UUID.
     *
     * @param player The player to check
     * @return boolean If the list contains player uuid
     */
    public boolean allows(Player player) {
        if (mode.equals(FilterMode.WHITELIST)) {
            return players.contains(player.getUniqueId().toString());
        } else {
            return players.isEmpty() || !players.contains(player.getUniqueId().toString());
        }
    }

    /**
     * Get if list contains a given block type.
     *
     * @param blockType The block type to check represented as a string
     * @return boolean If the list contains block type
     */
    public boolean allowsBlock(String blockType) {
        if (mode.equals(FilterMode.WHITELIST)) {
            return blocks.contains(blockType);
        } else {
            return blocks.isEmpty() || !blocks.contains(blockType);
        }
    }
}
