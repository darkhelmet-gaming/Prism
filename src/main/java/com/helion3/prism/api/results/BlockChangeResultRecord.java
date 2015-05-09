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

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;

/**
 * Represents a block change event record.
 */
public class BlockChangeResultRecord extends ResultRecordComplete implements Actionable {

    @Override
    public ActionableResult undo() {
        if (!data.isPresent()) {
            // @todo throw error
        }

        Optional<Location> optionalLoc = getLocation();
        if (!optionalLoc.isPresent()) {
            // @todo throw error
        }

        Location location = optionalLoc.get();

        Map<String,String> dataMap = data.get();

        Optional<BlockType> existingBlockType = Prism.getGame().getRegistry().getType(BlockType.class, dataMap.get("existingBlockId"));

        if (!existingBlockType.isPresent()) {
            // @todo throw error, handle
        }

        // @todo ensure location is available for a block

        // @todo the type registry is currently unimplemented
        location.replaceWith(BlockTypes.DIAMOND_BLOCK);

        // @todo add replacement block logic

        return new ActionableResult(true);
    }

    @Override
    public ActionableResult redo() {
        return new ActionableResult(false);
    }
}
