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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.IgniteableData;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntitySnapshot;
import org.spongepowered.api.entity.EntitySnapshot.Builder;

import com.helion3.prism.util.DataQueries;

public class EntityResult extends ResultComplete implements Actionable {
    @Override
    public ActionableResult rollback() {

        DataView entityData = formatEntityData();

        Optional<EntitySnapshot> snapshot = Sponge.getRegistry().createBuilder(Builder.class).build(entityData);
        if (!snapshot.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID);
        }

        Optional<Entity> entity = snapshot.get().restore();
        if (!entity.isPresent()) {
            return ActionableResult.skipped(SkipReason.INVALID);
        }

        // Don't let it burn to death (again?)
        entity.get().get(IgniteableData.class).ifPresent(data -> entity.get().offer(data.fireTicks().set(0)));

        // Heal, it was probably killed.
        entity.get().get(HealthData.class).ifPresent(data -> entity.get().offer(data.health().set(data.maxHealth().get())));

        return ActionableResult.success(new Transaction<>(new SerializableNonExistent(), entity.get()));
    }

    @Override
    public ActionableResult restore() {
        return ActionableResult.skipped(SkipReason.UNIMPLEMENTED);
    }

    private DataView formatEntityData() {
        DataView entity = data.getView(DataQueries.Entity).get();

        // Restore Position
        DataView location = data.getView(DataQueries.Location).get();
        entity.set(DataQueries.WorldUuid, location.getString(DataQueries.WorldUuid).get());
        location.remove(DataQueries.WorldUuid);
        entity.set(DataQueries.Position, location);

        // UnsafeData
        DataView unsafe = entity.getView(DataQueries.UnsafeData).get();

        List<Double> coordinates = new ArrayList<>();
        coordinates.add(location.getDouble(DataQueries.X).get());
        coordinates.add(location.getDouble(DataQueries.Y).get());
        coordinates.add(location.getDouble(DataQueries.Z).get());
        unsafe.set(DataQueries.Pos, coordinates);

        DataView rotation = entity.getView(DataQueries.Rotation).get();
        /* @todo For now this is commented out as we are not using it yet.
        List<Double> rot = new ArrayList<>();
        rot.add(rotation.getDouble(DataQueries.Y).get());
        rot.add(rotation.getDouble(DataQueries.Z).get());
        */
        unsafe.set(DataQueries.Rotation, rotation);

        entity.set(DataQueries.UnsafeData, unsafe);

        return entity;
    }
}
