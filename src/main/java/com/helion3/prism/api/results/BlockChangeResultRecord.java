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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.MemoryDataView;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.utils.DataQueries;
import com.helion3.prism.utils.DataUtils;

/**
 * Represents a block change event record.
 */
public class BlockChangeResultRecord extends ResultRecordComplete implements Actionable {
    @Override
    public ActionableResult undo() {
        Optional<Object> optionalOriginal = data.get(DataQueries.OriginalBlock);

        if (!optionalOriginal.isPresent()) {
            // @todo error/skip
        }

        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView restoration = ((DataView) optionalOriginal.get()).copy();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
            // @todo error/skip
        }

        DataView location = (MemoryDataView) optionalLocation.get();
        DataView position = new MemoryDataContainer();
        position.set(DataQueries.X, location.get(DataQueries.X).get());
        position.set(DataQueries.Y, location.get(DataQueries.Y).get());
        position.set(DataQueries.Z, location.get(DataQueries.Z).get());
        restoration.set(DataQueries.Position, position);
        restoration.set(DataQueries.WorldUuid, location.get(DataQueries.WorldUuid).get());

        // Unsafe data includes coordinates
        Optional<Object> optionalUnsafeData = restoration.get(DataQueries.UnsafeData);
        if (optionalUnsafeData.isPresent()) {
            DataView unsafeData = (DataView) optionalUnsafeData.get();
            unsafeData.set(DataQueries.X, location.get(DataQueries.X).get());
            unsafeData.set(DataQueries.Y, location.get(DataQueries.Y).get());
            unsafeData.set(DataQueries.Z, location.get(DataQueries.Z).get());
            restoration.set(DataQueries.UnsafeData, unsafeData);
        }

        Prism.getLogger().debug(DataUtils.jsonFromDataView(restoration).toString());

        // DataContainer -> BlockSnapshot
        Optional<BlockSnapshot> optionalSnapshot = Prism.getGame().getRegistry().createBlockSnapshotBuilder().build(restoration);
        if (!optionalSnapshot.isPresent()) {
            // @todo error/skip
        }

        // Actually restore!
        if (!optionalSnapshot.get().restore(true, true)) {
            // @todo error/skip
        }

        return new ActionableResult();
    }

    @Override
    public ActionableResult redo() {
        return new ActionableResult();
    }
}
