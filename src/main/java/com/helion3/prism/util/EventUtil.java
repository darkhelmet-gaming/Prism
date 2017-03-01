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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.entity.living.player.Player;
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
    public static boolean rejectBreakEventIdentity(BlockType a, BlockType b, Cause cause) {
        // Falling blocks
        if (a.equals(BlockTypes.GRAVEL) && b.equals(BlockTypes.AIR)) {
            return !cause.first(Player.class).isPresent();
        }

        // Interesting bugs...
        if (a.equals(BlockTypes.AIR) && b.equals(BlockTypes.AIR)) {
            return true;
        }

        return false;
    }

    /**
     * Reject certain events which can only be identified
     * by the change + cause signature.
     *
     * @param a BlockType original
     * @param b BlockType replacement
     * @param cause Cause chain from event
     * @return boolean If should be rejected
     */
    public static boolean rejectPlaceEventIdentity(BlockType a, BlockType b, Cause cause) {
        // Things that eat grass...
        if (a.equals(BlockTypes.GRASS) && b.equals(BlockTypes.DIRT)) {
            return cause.first(Living.class).isPresent();
        }

        // Grass-like "Grow" events
        if (a.equals(BlockTypes.DIRT) && b.equals(BlockTypes.GRASS)) {
            return cause.first(BlockSnapshot.class).isPresent();
        }

        // If no entity at fault, we don't care about placement that didn't affect anything
        if (!cause.first(Entity.class).isPresent()) {
            return (a.equals(BlockTypes.AIR));
        }

        // Natural flow/fire.
        // Note: This only allows tracking on the source block set by a player using
        // buckets, or items to set fires. Blocks broken by water, lava or fire are still logged as usual.
        // Full flow/fire tracking would be hard on the database and is generally unnecessary.
        if (!cause.first(Player.class).isPresent()) {
            return (a.equals(BlockTypes.AIR) && (b.equals(BlockTypes.FLOWING_LAVA) || b.equals(BlockTypes.FLOWING_WATER)) ||
            b.equals(BlockTypes.FIRE));
        }

        return false;
    }
}
