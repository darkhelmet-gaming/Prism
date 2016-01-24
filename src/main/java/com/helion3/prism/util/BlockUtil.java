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

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;

public class BlockUtil {
    private BlockUtil() {}

    /**
     * Sponge's ChangeBlockEvent.Place covers a lot, but it also includes a lot
     * we don't want. So here we can setup checks to filter out block combinations.
     *
     * @param a BlockType original
     * @param b BlockType final
     * @return boolean True if combo should be rejected
     */
    public static boolean rejectPlaceCombination(BlockType a, BlockType b) {
        return (
            // Just basic state changes
            a.equals(BlockTypes.LIT_FURNACE) || b.equals(BlockTypes.LIT_FURNACE) ||
            a.equals(BlockTypes.LIT_REDSTONE_LAMP) || b.equals(BlockTypes.LIT_REDSTONE_LAMP) ||
            a.equals(BlockTypes.LIT_REDSTONE_ORE) || b.equals(BlockTypes.LIT_REDSTONE_ORE) ||

            // It's all water...
            (a.equals(BlockTypes.WATER) && b.equals(BlockTypes.FLOWING_WATER)) ||
            (a.equals(BlockTypes.FLOWING_WATER) && b.equals(BlockTypes.WATER)) ||

            // It's all lava....
            (a.equals(BlockTypes.LAVA) && b.equals(BlockTypes.FLOWING_LAVA)) ||
            (a.equals(BlockTypes.FLOWING_LAVA) && b.equals(BlockTypes.LAVA)) ||

            // Crap that fell into lava
            (a.equals(BlockTypes.LAVA) && b.equals(BlockTypes.GRAVEL)) ||
            (a.equals(BlockTypes.FLOWING_LAVA) && b.equals(BlockTypes.GRAVEL)) ||
            (a.equals(BlockTypes.LAVA) && b.equals(BlockTypes.SAND)) ||
            (a.equals(BlockTypes.FLOWING_LAVA) && b.equals(BlockTypes.SAND)) ||

            // It's fire...
            (a.equals(BlockTypes.FIRE) && b.equals(BlockTypes.AIR)) ||

            // You can't place air
            b.equals(BlockTypes.AIR)
        );
    }

    /**
     * Sponge's ChangeBlockEvent.Break covers a lot, but it also includes a lot
     * we don't want. So here we can setup checks to filter out block combinations.
     *
     * @param a BlockType original
     * @param b BlockType final
     * @return boolean True if combo should be rejected
     */
    public static boolean rejectBreakCombination(BlockType a, BlockType b) {
        return (
            // You can't break these...
            a.equals(BlockTypes.FIRE) ||
            a.equals(BlockTypes.AIR)
        );
    }
}
