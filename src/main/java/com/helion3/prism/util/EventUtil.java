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
package com.helion3.prism.util;

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.living.animal.Sheep;
import org.spongepowered.api.event.cause.Cause;

public class EventUtil {
    private EventUtil() {}

    /**
     * Reject certain events which can only be identified
     * by the change + cause signature.
     *
     * @param a BlockType original
     * @param b BlockType replacement
     * @param cause Cause chain from event
     * @return boolean If should be rejected
     */
    public static boolean rejectEventIdentity(BlockType a, BlockType b, Cause cause) {
        // Things that eat grass...
        if (a.equals(BlockTypes.GRASS) && b.equals(BlockTypes.DIRT)) {
            Optional<Sheep> sheep = cause.first(Sheep.class);
            return sheep.isPresent();
        }

        // Grass-like "Grow" events
        if (a.equals(BlockTypes.DIRT) && b.equals(BlockTypes.GRASS)) {
            Optional<BlockSnapshot> snapshot = cause.first(BlockSnapshot.class);
            return snapshot.isPresent();
        }

        return false;
    }
}
