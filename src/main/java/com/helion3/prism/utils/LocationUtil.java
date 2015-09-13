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
package com.helion3.prism.utils;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;

public class LocationUtil {

    private LocationUtil() {}

    /**
     * Determines if given location will accept a block change. If the current
     * block type is replaceable, or if the block we expect to be there is.
     *
     * @param location Location to check.
     * @param originalBlockType Optional expected block type.
     * @return If a location accepts a change.
     */
    public static boolean locationAllowsChange(Location<?> location, Optional<BlockType> originalBlockType) {
        boolean locationAllowsPlacement = location.getBlockType().isReplaceable();

        if (!locationAllowsPlacement) {
            // Location has a solid block. Might still be ok if we know about it...
            if (originalBlockType.isPresent()) {
                if (location.getBlockType().equals(originalBlockType.get())) {
                    locationAllowsPlacement = true;
                    System.out.println("existing location type matches replacement");
                }
            }
        }

        return locationAllowsPlacement;
    }
}
