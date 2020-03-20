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
package com.helion3.prism.api.query;

import java.util.ArrayList;
import java.util.List;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.Location;

import com.google.common.collect.Range;
import com.helion3.prism.util.DataQueries;
import org.spongepowered.api.world.extent.Extent;

/**
 * Contains a group of conditions. Each group should be compared
 * separately, and each field within each group should be compared
 * as part of the group.
 */
public final class ConditionGroup implements Condition {
    private final List<Condition> conditions = new ArrayList<>();
    private final Operator operator;

    public enum Operator {
        AND, OR
    }

    /**
     * Create a new group with a specific operator.
     * @param operator
     */
    public ConditionGroup(Operator operator) {
        this.operator = operator;
    }

    /**
     * Add a condition.
     *
     * @param condition Condition
     */
    public void add(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Add a list of conditions.
     *
     * @param conditions List of conditions.
     */
    public void add(List<Condition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * Get all conditions.
     *
     * @return List<Condition>
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * Get the operator for this groups rules.
     *
     * @return Operator
     */
    public Operator getOperator() {
        return operator;
    }

    /**
     * Convenience method to build conditions for a single location.
     *
     * @param location Location<?>
     * @return ConditionGroup
     */
    public static ConditionGroup from(Location<?> location) {
        ConditionGroup conditions = new ConditionGroup(Operator.AND);

        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.WorldUuid), MatchRule.EQUALS, location.getExtent().getUniqueId().toString()));
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.X), MatchRule.EQUALS, location.getBlockX()));
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.Y), MatchRule.EQUALS, location.getBlockY()));
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.Z), MatchRule.EQUALS, location.getBlockZ()));

        return conditions;
    }

    /**
     * Convenience method to build conditions for a region of radius around a central location.
     *
     * @param location Location<?>
     * @param radius Integer
     * @return ConditionGroup
     */
    public static ConditionGroup from(Location<?> location, int radius) {
        ConditionGroup conditions = new ConditionGroup(Operator.AND);

        // World
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.WorldUuid), MatchRule.EQUALS, location.getExtent().getUniqueId().toString()));

        // X
        Range<Integer> xRange = Range.open(location.getBlockX() - radius, location.getBlockX() + radius);
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.X), xRange));

        // Y
        Range<Integer> yRange = Range.open(location.getBlockY() - radius, location.getBlockY() + radius);
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.Y), yRange));

        // Z
        Range<Integer> zRange = Range.open(location.getBlockZ() - radius, location.getBlockZ() + radius);
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.Z), zRange));

        return conditions;
    }

    /**
     * Convenience method to build conditions for a prismatic region marked with two corners.
     *
     * @param extent Extent of both block locations
     * @param blockLocation1 vector describing first location
     * @param blockLocation2 vector describing second location
     * @return ConditionGroup
     */
    public static ConditionGroup from(Extent extent, Vector3i blockLocation1, Vector3i blockLocation2) {
        ConditionGroup conditions = new ConditionGroup(Operator.AND);

        // World
        conditions.add(FieldCondition.of(DataQueries.Location.then(DataQueries.WorldUuid), MatchRule.EQUALS, extent.getUniqueId().toString()));

        // X
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.X),
            MatchRule.GREATER_THAN_EQUAL,
            Math.min(blockLocation1.getX(), blockLocation2.getX())));
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.X),
            MatchRule.LESS_THAN_EQUAL,
            Math.max(blockLocation1.getX(), blockLocation2.getX())));

        // Y
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.Y),
            MatchRule.GREATER_THAN_EQUAL,
            Math.min(blockLocation1.getY(), blockLocation2.getY())));
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.Y),
            MatchRule.LESS_THAN_EQUAL,
            Math.max(blockLocation1.getY(), blockLocation2.getY())));

        // Z
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.Z),
            MatchRule.GREATER_THAN_EQUAL,
            Math.min(blockLocation1.getZ(), blockLocation2.getZ())));
        conditions.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.Z),
            MatchRule.LESS_THAN_EQUAL,
            Math.max(blockLocation1.getZ(), blockLocation2.getZ())));

        return conditions;
    }
}
