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
package com.helion3.prism.api.records;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.base.Preconditions;
import com.helion3.prism.Prism;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.block.BlockSnapshot.Builder;

import com.helion3.prism.util.BlockUtil;
import com.helion3.prism.util.DataQueries;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.world.BlockChangeFlags;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import javax.annotation.Nonnull;

/**
 * Represents a block change event record.
 */
public class BlockResult extends ResultComplete implements Actionable {
    @Override
    public CompletableFuture<ActionableResult> rollback() throws Exception {
        CompletableFuture<ActionableResult> result = new CompletableFuture<>();

        Optional<Object> optionalOriginal = data.get(DataQueries.OriginalBlock);

        if (!optionalOriginal.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID));
            return result;
        }

        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView finalBlock = ((DataView) optionalOriginal.get()).copy();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID_LOCATION));
            return result;
        }

        // Format
        finalBlock = formatBlockData(finalBlock, optionalLocation.get());

        Optional<BlockSnapshot> optionalSnapshot = Sponge.getRegistry().createBuilder(Builder.class).build(finalBlock);
        if (!optionalSnapshot.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID));
            return result;
        }

        BlockSnapshot snapshot = optionalSnapshot.get();

        if (!snapshot.getLocation().isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID_LOCATION));
            return result;
        }

        Location<World> location = snapshot.getLocation().get();

        // Filter unsafe blocks
        if (BlockUtil.rejectIllegalApplierBlock(snapshot.getState().getType())) {
            result.complete(ActionableResult.skipped(SkipReason.ILLEGAL_BLOCK));
            return result;
        }

        Task.builder().execute(() -> {
            // Current block in this space.
            BlockSnapshot original = location.getBlock().snapshotFor(location);

            // Actually restore!
            if (!optionalSnapshot.get().restore(true, BlockChangeFlags.NONE)) {
                result.complete(ActionableResult.skipped(SkipReason.UNKNOWN));
                return;
            }

            // Final block in this space.
            BlockSnapshot resultingBlock = location.getBlock().snapshotFor(location);

            result.complete(ActionableResult.success(new Transaction<>(original, resultingBlock)));
        }).submit(Prism.getInstance());

        return result;
    }

    public DataView formatBlockData(DataView finalBlock, @Nonnull Object optionalLocation) {
        Preconditions.checkNotNull(optionalLocation, "The location you are formatting cannot be null.");
        DataView location = (DataView) optionalLocation;
        DataView position = DataContainer.createNew();
        position.set(DataQueries.X, location.get(DataQueries.X).get());
        position.set(DataQueries.Y, location.get(DataQueries.Y).get());
        position.set(DataQueries.Z, location.get(DataQueries.Z).get());
        finalBlock.set(DataQueries.Position, position);
        finalBlock.set(DataQueries.WorldUuid, location.get(DataQueries.WorldUuid).get());

        int blockStateVersion = finalBlock.getInt(DataQueries.BlockState.then(DataQueries.ContentVersion)).orElse(1);
        if (blockStateVersion == 1) {
            // Unsafe data includes coordinates
            Optional<Object> optionalUnsafeData = finalBlock.get(DataQueries.UnsafeData);
            if (optionalUnsafeData.isPresent()) {
                DataView unsafeData = (DataView) optionalUnsafeData.get();
                unsafeData.set(DataQueries.X, location.get(DataQueries.X).get());
                unsafeData.set(DataQueries.Y, location.get(DataQueries.Y).get());
                unsafeData.set(DataQueries.Z, location.get(DataQueries.Z).get());
                finalBlock.set(DataQueries.UnsafeData, unsafeData);
            }
        }

        return finalBlock;
    }

    @Override
    public CompletableFuture<ActionableResult> restore() throws Exception {

        CompletableFuture<ActionableResult> result = new CompletableFuture<>();

        Optional<Object> optionalFinal = data.get(DataQueries.ReplacementBlock);
        if (!optionalFinal.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID));
            return result;
        }

        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView finalBlock = ((DataView) optionalFinal.get()).copy();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID_LOCATION));
            return result;
        }

        // Format
        finalBlock = formatBlockData(finalBlock, optionalLocation.get());

        Optional<BlockSnapshot> optionalSnapshot = Sponge.getRegistry().createBuilder(Builder.class).build(finalBlock);
        if (!optionalSnapshot.isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID));
            return result;
        }

        BlockSnapshot snapshot = optionalSnapshot.get();

        if (!snapshot.getLocation().isPresent()) {
            result.complete(ActionableResult.skipped(SkipReason.INVALID_LOCATION));
            return result;
        }

        Location<World> location = snapshot.getLocation().get();

        // Filter unsafe blocks
        if (BlockUtil.rejectIllegalApplierBlock(snapshot.getState().getType())) {
            result.complete(ActionableResult.skipped(SkipReason.ILLEGAL_BLOCK));
            return result;
        }

        // Current block in this space.
        BlockSnapshot original = location.getBlock().snapshotFor(location);

        Task.builder().execute(() -> {
            // Actually restore!
            if (!optionalSnapshot.get().restore(true, BlockChangeFlags.NONE)) {
                result.complete(ActionableResult.skipped(SkipReason.UNKNOWN));
                return;
            }

            // Final block in this space.
            BlockSnapshot resultingBlock = location.getBlock().snapshotFor(location);
            result.complete(ActionableResult.success(new Transaction<>(original, resultingBlock)));

        }).submit(Prism.getInstance());

        return result;

    }
}
