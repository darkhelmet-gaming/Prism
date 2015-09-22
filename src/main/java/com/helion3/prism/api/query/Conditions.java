package com.helion3.prism.api.query;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.world.Location;

import com.google.common.collect.Range;
import com.helion3.prism.utils.DataQueries;

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
        conditions.add(Condition.of(fieldPath + DataQueries.x, MatchRule.EQUALS, location.getBlockX()));
        conditions.add(Condition.of(fieldPath + DataQueries.y, MatchRule.EQUALS, location.getBlockY()));
        conditions.add(Condition.of(fieldPath + DataQueries.z, MatchRule.EQUALS, location.getBlockZ()));

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
        conditions.add(Condition.of(fieldPath + DataQueries.x, xRange));

        // Y
        Range<Integer> yRange = Range.open(location.getBlockY() - radius, location.getBlockY() + radius);
        conditions.add(Condition.of(fieldPath + DataQueries.y, yRange));

        // Z
        Range<Integer> zRange = Range.open(location.getBlockZ() - radius, location.getBlockZ() + radius);
        conditions.add(Condition.of(fieldPath + DataQueries.z, zRange));

        return conditions;
    }
}
