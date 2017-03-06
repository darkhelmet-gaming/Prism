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
package com.helion3.prism.util;

import java.util.Collection;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class WorldUtil {
    private WorldUtil() {}

    public static int removeIllegalBlocks(Location<World> location, int radius, Cause cause) {
        final World world = location.getExtent();

        int xMin = location.getBlockX() - radius;
        int xMax = location.getBlockX() + radius;

        int zMin = location.getBlockZ() - radius;
        int zMax = location.getBlockZ() + radius;

        int yMin = location.getBlockY() - radius;
        int yMax = location.getBlockY() + radius;

        // Clamp Y basement
        if (yMin < 0) {
            yMin = 0;
        }

        // Clamp Y ceiling
        if (yMax >= world.getDimension().getBuildHeight()) {
            yMax = world.getDimension().getBuildHeight();
        }

        int changeCount = 0;
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    BlockType existing = world.getBlock(x, y, z).getType();
                    if (existing.equals(BlockTypes.FIRE) || existing.equals(BlockTypes.TNT)) {
                        world.setBlockType(x, y, z, BlockTypes.AIR, cause);
                        changeCount++;
                    }
                }
            }
        }

        return changeCount;
    }

    /**
     * Remove a specific block from a given radius around a region.
     *
     * @param type BlockType to remove.
     * @param location Location center
     * @param radius Integer radius around location
     * @param cause Cause
     * @return integer Count of removals
     */
    public static int removeAroundFromLocation(BlockType type, Location<World> location, int radius, Cause cause) {
        final World world = location.getExtent();

        int xMin = location.getBlockX() - radius;
        int xMax = location.getBlockX() + radius;

        int zMin = location.getBlockZ() - radius;
        int zMax = location.getBlockZ() + radius;

        int yMin = location.getBlockY() - radius;
        int yMax = location.getBlockY() + radius;

        // Clamp Y basement
        if (yMin < 0) {
            yMin = 0;
        }

        // Clamp Y ceiling
        if (yMax >= world.getDimension().getBuildHeight()) {
            yMax = world.getDimension().getBuildHeight();
        }

        int changeCount = 0;
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                for (int y = yMin; y <= yMax; y++) {
                    if (world.getBlock(x, y, z).getType().equals(type)) {
                        world.setBlockType(x, y, z, BlockTypes.AIR, cause);
                        changeCount++;
                    }
                }
            }
        }

        return changeCount;
    }

    /**
     * Removes all item entities in a radius around a given a location.
     *
     * @param location Location center
     * @param radius Integer radius around location
     * @return integer Count of removals
     */
    public static int removeItemEntitiesAroundLocation(Location<World> location, int radius) {
        int xMin = location.getBlockX() - radius;
        int xMax = location.getBlockX() + radius;

        int zMin = location.getBlockZ() - radius;
        int zMax = location.getBlockZ() + radius;

        int yMin = location.getBlockY() - radius;
        int yMax = location.getBlockY() + radius;

        Collection<Entity> entities = location.getExtent().getEntities(e -> {
            Location<World> loc = e.getLocation();

            return (e.getType().equals(EntityTypes.ITEM) &&
                    (loc.getX() > xMin && loc.getX() <= xMax) &&
                    (loc.getY() > yMin && loc.getY() <= yMax) &&
                    (loc.getZ() > zMin && loc.getZ() <= zMax)
            );
        });

        if (!entities.isEmpty()) {
            entities.forEach(Entity::remove);
        }

        return entities.size();
    }

    /**
     * Drain all liquids from a radius around a given location.
     *
     * @param location Location center
     * @param radius Integer radius around location
     * @param cause Cause
     * @return integer Count of removals
     */
    public static int removeLiquidsAroundLocation(Location<World> location, int radius, Cause cause) {
        int changeCount = 0;
        for (BlockType liquid : BlockUtil.getLiquidBlockTypes()) {
            changeCount = removeAroundFromLocation(liquid, location, radius, cause);
        }

        return changeCount;
    }
}
