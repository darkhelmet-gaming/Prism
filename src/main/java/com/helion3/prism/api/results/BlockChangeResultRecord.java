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

import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.Location;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.utils.LocationUtil;

/**
 * Represents a block change event record.
 */
public class BlockChangeResultRecord extends ResultRecordComplete implements Actionable {

    @Override
    public ActionableResult undo() {
//        // Location
//        Optional<Location> optionalLocation = getLocation();
//        if (!optionalLocation.isPresent()) {
//            return new ActionableResult(SkipReason.INVALID_LOCATION);
//        }
//
//        Location location = optionalLocation.get();
//
//        // Existing/replacement block IDs
//        Optional<String> optionalExistingBlockId = data.getString(DataQuery.of("location", "BlockType"));
//        Optional<String> optionalReplacementBlockId = data.getString(DataQuery.of("state", "BlockType"));
//
//        // Sponge currently doesn't support the "minecraft:" namespace...
//        String existingId = "air";
//        if (optionalExistingBlockId.isPresent()) {
//            existingId = optionalExistingBlockId.get().replace("minecraft:", "");
//        }
//
//        String replacementId = "air";
//        if (optionalReplacementBlockId.isPresent()) {
//            replacementId = optionalReplacementBlockId.get().replace("minecraft:", "");
//        }
//
//        // ids -> BlockType
//        Optional<BlockType> existingBlockType = Prism.getGame().getRegistry().getType(BlockType.class, existingId);
//        Optional<BlockType> replacementBlockType = Prism.getGame().getRegistry().getType(BlockType.class, replacementId);
//        if (!existingBlockType.isPresent() && !replacementBlockType.isPresent()) {
//            return new ActionableResult(SkipReason.INVALID_BLOCK);
//        }
//
//        if (!LocationUtil.locationAllowsChange(location, replacementBlockType)) {
//            return new ActionableResult(SkipReason.OCCUPIED);
//        }
//
//        location.setBlockType(existingBlockType.get());

        return new ActionableResult();
    }

    @Override
    public ActionableResult redo() {
        return new ActionableResult();
    }
}
