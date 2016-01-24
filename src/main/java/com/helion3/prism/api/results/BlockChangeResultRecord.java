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

import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.MemoryDataView;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.block.BlockSnapshot.Builder;

import com.helion3.prism.Prism;
import com.helion3.prism.util.BlockUtil;
import com.helion3.prism.util.DataQueries;

/**
 * Represents a block change event record.
 */
public class BlockChangeResultRecord extends ResultRecordComplete implements Actionable {
    @Override
    public ActionableResult rollback() {
        Optional<Object> optionalOriginal = data.get(DataQueries.OriginalBlock);

        if (!optionalOriginal.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_BLOCK);
        }

        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView finalBlock = ((DataView) optionalOriginal.get()).copy();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_LOCATION);
        }

        // Format
        finalBlock = formatBlockData(finalBlock, optionalLocation);

        Optional<BlockSnapshot> optionalSnapshot = Prism.getGame().getRegistry().createBuilder(Builder.class).build(finalBlock);
        if (!optionalSnapshot.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_BLOCK);
        }

        BlockSnapshot snapshot = optionalSnapshot.get();

        // Filter unsafe blocks
        if (BlockUtil.rejectIllegalApplierBlock(snapshot.getState().getType())) {
            return ActionableResult.skipped(SkipReason.ILLEGAL_BLOCK);
        }

        // Current block in this space.
        BlockSnapshot original = snapshot.getLocation().get().getBlock().snapshotFor(snapshot.getLocation().get());

        // Actually restore!
        if (!optionalSnapshot.get().restore(true, true)) {
            return ActionableResult.skipped(SkipReason.UNKNOWN);
        }

        // Final block in this space.
        BlockSnapshot resultingBlock = snapshot.getLocation().get().getBlock().snapshotFor(snapshot.getLocation().get());

        return ActionableResult.success(new Transaction<BlockSnapshot>(original, resultingBlock));
    }

    public DataView formatBlockData(DataView finalBlock, Optional<Object> optionalLocation) {
        DataView location = (MemoryDataView) optionalLocation.get();
        DataView position = new MemoryDataContainer();
        position.set(DataQueries.X, location.get(DataQueries.X).get());
        position.set(DataQueries.Y, location.get(DataQueries.Y).get());
        position.set(DataQueries.Z, location.get(DataQueries.Z).get());
        finalBlock.set(DataQueries.Position, position);
        finalBlock.set(DataQueries.WorldUuid, location.get(DataQueries.WorldUuid).get());

        // Unsafe data includes coordinates
        Optional<Object> optionalUnsafeData = finalBlock.get(DataQueries.UnsafeData);
        if (optionalUnsafeData.isPresent()) {
            DataView unsafeData = (DataView) optionalUnsafeData.get();
            unsafeData.set(DataQueries.X, location.get(DataQueries.X).get());
            unsafeData.set(DataQueries.Y, location.get(DataQueries.Y).get());
            unsafeData.set(DataQueries.Z, location.get(DataQueries.Z).get());
            finalBlock.set(DataQueries.UnsafeData, unsafeData);
        }

        return finalBlock;
    }

    @Override
    public ActionableResult restore() {
        Optional<Object> optionalFinal = data.get(DataQueries.ReplacementBlock);
        if (!optionalFinal.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_BLOCK);
        }

        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView finalBlock = ((DataView) optionalFinal.get()).copy();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_LOCATION);
        }

        // Format
        finalBlock = formatBlockData(finalBlock, optionalLocation);

        Optional<BlockSnapshot> optionalSnapshot = Prism.getGame().getRegistry().createBuilder(Builder.class).build(finalBlock);
        if (!optionalSnapshot.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID_BLOCK);
        }

        BlockSnapshot snapshot = optionalSnapshot.get();

        // Filter unsafe blocks
        if (BlockUtil.rejectIllegalApplierBlock(snapshot.getState().getType())) {
            return ActionableResult.skipped(SkipReason.ILLEGAL_BLOCK);
        }

        // Current block in this space.
        BlockSnapshot original = snapshot.getLocation().get().getBlock().snapshotFor(snapshot.getLocation().get());

        // Actually restore!
        if (!optionalSnapshot.get().restore(true, true)) {
            return ActionableResult.skipped(SkipReason.UNKNOWN);
        }

        // Final block in this space.
        BlockSnapshot resultingBlock = snapshot.getLocation().get().getBlock().snapshotFor(snapshot.getLocation().get());

        return ActionableResult.success(new Transaction<BlockSnapshot>(original, resultingBlock));
    }
}
