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
package com.helion3.prism.api.results;

import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;

/**
 * Represents a complete copy of an event record data from
 * a query result. Used for displaying individual entries
 * or for non-lookup actions.
 *
 */
public class ResultRecordComplete extends ResultRecord {

    /**
     * UUID representing the world the event occurred in
     */
    public Optional<UUID> world;

    /**
     * "x" value of a coordinate
     */
    public Optional<Double> x;

    /**
     * "y" value of a coordinate
     */
    public Optional<Double> y;

    /**
     * "x" value of a coordinate
     */
    public Optional<Double> z;

    /**
     * Map of additional data key/values
     */
    public Optional<Map<String,String>> data;

    /**
     *
     * @return
     */
    public Optional<Location> getLocation() {
        Location location = null;
        if (world.isPresent()) {
            Optional<World> optionalWorld = Prism.getGame().getServer().getWorld(world.get());
            if (optionalWorld.isPresent() && x.isPresent() && y.isPresent() && z.isPresent()) {
                World world = optionalWorld.get();
                location = new Location(world, x.get(), y.get(), z.get());
            }
        }
        return Optional.fromNullable(location);
    }
}