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
package com.helion3.prism.api.query;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.world.Location;

import com.google.common.collect.Range;
import com.helion3.prism.util.DataQueries;

public class Conditions {
    private Conditions() {}

    /**
     *
     * @param location
     * @return
     */
    public static List<Condition> from(Location<?> location) {
        List<Condition> conditions = new ArrayList<Condition>();

        String fieldPath = DataQueries.Location.toString() + ".";

        conditions.add(new Condition(fieldPath + DataQueries.WorldUuid.toString(), MatchRule.INCLUDES, location.getExtent().getUniqueId().toString()));
        conditions.add(Condition.of(fieldPath + DataQueries.X, MatchRule.EQUALS, location.getBlockX()));
        conditions.add(Condition.of(fieldPath + DataQueries.Y, MatchRule.EQUALS, location.getBlockY()));
        conditions.add(Condition.of(fieldPath + DataQueries.Z, MatchRule.EQUALS, location.getBlockZ()));

        return conditions;
    }

    /**
     *
     * @param location
     * @param radius
     * @return
     */
    public static List<Condition> from(Location<?> location, int radius) {
        List<Condition> conditions = new ArrayList<Condition>();

        String fieldPath = DataQueries.Location.toString() + ".";

        // World
        conditions.add(new Condition(fieldPath + DataQueries.WorldUuid.toString(), MatchRule.INCLUDES, location.getExtent().getUniqueId().toString()));

        // X
        Range<Integer> xRange = Range.open(location.getBlockX() - radius, location.getBlockX() + radius);
        conditions.add(Condition.of(fieldPath + DataQueries.X, xRange));

        // Y
        Range<Integer> yRange = Range.open(location.getBlockY() - radius, location.getBlockY() + radius);
        conditions.add(Condition.of(fieldPath + DataQueries.Y, yRange));

        // Z
        Range<Integer> zRange = Range.open(location.getBlockZ() - radius, location.getBlockZ() + radius);
        conditions.add(Condition.of(fieldPath + DataQueries.Z, zRange));

        return conditions;
    }
}
