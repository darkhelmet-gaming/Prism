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
import com.google.common.collect.Lists;
import com.helion3.prism.Prism;
import com.helion3.prism.utils.DataQueries;

/**
 * Represents a block change event record.
 */
public class BlockChangeResultRecord extends ResultRecordComplete implements Actionable {

    @Override
    public ActionableResult undo() {
        // Our data is stored with a different structure, so we'll need
        // a little manual effort to reformat it.
        DataView restoration = new MemoryDataContainer();

        // Build World UUID / Vec3 data BlockSnapshot expects
        Optional<Object> optionalLocation = data.get(DataQueries.Location);
        if (!optionalLocation.isPresent()) {
         // @todo error/skip
        }

        DataView location = (MemoryDataView) optionalLocation.get();
        DataView position = new MemoryDataContainer();
        position.set(DataQueries.X, location.get(DataQueries.x).get());
        position.set(DataQueries.Y, location.get(DataQueries.y).get());
        position.set(DataQueries.Z, location.get(DataQueries.z).get());
        restoration.set(DataQueries.Position, position);
        restoration.set(DataQueries.WorldUuid, location.get(DataQueries.WorldUuid).get());

        // Build BlockState data BlockSnapshot expects
        Optional<Object> optionalOriginalBlock = data.get(DataQueries.OriginalBlock);
        if (!optionalOriginalBlock.isPresent()) {
         // @todo error/skip
        }

        DataView original = (DataView) optionalOriginalBlock.get();

        // Store extra data
        Optional<Object> extra = original.get(DataQueries.ExtraData);
        if (extra.isPresent()) {
            restoration.set(DataQueries.ExtraData, original.get(DataQueries.ExtraData).get());
            original.remove(DataQueries.ExtraData);
        } else {
            // @todo hopefully sponge can help me avoid this line
            restoration.set(DataQueries.ExtraData, Lists.newArrayList());
        }

        // Provide empty Data list
        // @todo hopefully sponge can help me avoid this line
        if (!original.get(DataQueries.Data).isPresent()) {
            original.set(DataQueries.Data, Lists.newArrayList());
        }

        restoration.set(DataQueries.BlockState, original);

        // DataContainer -> BlockSnapshot
        Optional<BlockSnapshot> optionalSnapshot = Prism.getGame().getRegistry().createBlockSnapshotBuilder().build(restoration);
        if (!optionalSnapshot.isPresent()) {
            // @todo error/skip
        }

        // Actually restore!
        optionalSnapshot.get().restore(true, true);

        return new ActionableResult();
    }

    @Override
    public ActionableResult redo() {
        return new ActionableResult();
    }
}
