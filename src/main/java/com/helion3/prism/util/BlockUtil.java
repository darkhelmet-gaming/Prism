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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.property.block.MatterProperty;
import org.spongepowered.api.data.property.block.MatterProperty.Matter;

import com.helion3.prism.Prism;

public class BlockUtil {
    private BlockUtil() {}

    /**
     * Get a list of all LIQUID block types.
     *
     * @return List<BlockType>
     */
    public static List<BlockType> getLiquidBlockTypes() {
        List<BlockType> liquids = new ArrayList<>();

        Collection<BlockType> types = Prism.getGame().getRegistry().getAllOf(BlockType.class);
        for (BlockType type : types) {
            Optional<MatterProperty> property = type.getProperty(MatterProperty.class);
            if (property.isPresent() && Objects.equals(property.get().getValue(), Matter.LIQUID)) {
                liquids.add(type);
            }
        }

        // @todo Sponge has not yet implemented the MatterProperties...
        liquids.add(BlockTypes.LAVA);
        liquids.add(BlockTypes.FLOWING_LAVA);
        liquids.add(BlockTypes.WATER);
        liquids.add(BlockTypes.FLOWING_WATER);

        return liquids;
    }

    /**
     * Reject specific blocks from an applier because they're 99% going to do more harm.
     *
     * @param type BlockType
     * @return
     */
    public static boolean rejectIllegalApplierBlock(BlockType type) {
        return (type.equals(BlockTypes.FIRE) ||
                type.equals(BlockTypes.TNT) ||
                type.equals(BlockTypes.LAVA) ||
                type.equals(BlockTypes.FLOWING_LAVA));
    }

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
            a.equals(BlockTypes.UNLIT_REDSTONE_TORCH) || b.equals(BlockTypes.UNLIT_REDSTONE_TORCH) ||
            (a.equals(BlockTypes.POWERED_REPEATER) && b.equals(BlockTypes.UNPOWERED_REPEATER)) ||
            (a.equals(BlockTypes.UNPOWERED_REPEATER) && b.equals(BlockTypes.POWERED_REPEATER)) ||
            (a.equals(BlockTypes.POWERED_COMPARATOR) && b.equals(BlockTypes.UNPOWERED_COMPARATOR)) ||
            (a.equals(BlockTypes.UNPOWERED_COMPARATOR) && b.equals(BlockTypes.POWERED_COMPARATOR)) ||

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

            // It's fire (which didn't burn anything)
            (a.equals(BlockTypes.FIRE) && b.equals(BlockTypes.AIR)) ||

            // Piston
            a.equals(BlockTypes.PISTON_EXTENSION) || b.equals(BlockTypes.PISTON_EXTENSION) ||
            a.equals(BlockTypes.PISTON_HEAD) || b.equals(BlockTypes.PISTON_HEAD) ||

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
            a.equals(BlockTypes.AIR) ||

            // Note, see "natural flow" comment above.
            ((a.equals(BlockTypes.FLOWING_WATER) || a.equals(BlockTypes.FLOWING_LAVA)) && b.equals(BlockTypes.AIR)) ||

            // Piston
            a.equals(BlockTypes.PISTON_EXTENSION) || b.equals(BlockTypes.PISTON_EXTENSION) ||
            a.equals(BlockTypes.PISTON_HEAD) || b.equals(BlockTypes.PISTON_HEAD)
        );
    }
}
