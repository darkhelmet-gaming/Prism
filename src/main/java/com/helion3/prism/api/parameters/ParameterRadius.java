package com.helion3.prism.api.parameters;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.utils.DataQueries;

public class ParameterRadius extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    /**
     * Parameter handling a radius around a single location.
     */
    public ParameterRadius() {
        super(ImmutableList.of("r", "radius"));
    }

    @Override
    public boolean acceptsSource(@Nullable CommandSource source) {
        Optional<CommandSource> optional = Optional.ofNullable(source);

        return (optional.isPresent() && optional.get() instanceof Player);
    }

    @Override
    public boolean acceptsValue(String parameter) {
        return pattern.matcher(parameter).matches();
    }

    @Override
    public void process(QuerySession session, String value, Query query) {
        // @todo error on NumberFormatException
        int radius = Integer.parseInt(value);

        if (session.getCommandSource().get() instanceof Player) {
            Location<World> location = ((Player) session.getCommandSource().get()).getLocation();

            query.addCondition(new Condition(DataQueries.WorldUuid, MatchRule.INCLUDES, location.getExtent().getUniqueId().toString()));

            // Minimum
            query.addCondition(new Condition(DataQueries.x, MatchRule.GREATER_THAN_EQUAL, location.getBlockX() - radius));
            query.addCondition(new Condition(DataQueries.y, MatchRule.GREATER_THAN_EQUAL, location.getBlockY() - radius));
            query.addCondition(new Condition(DataQueries.z, MatchRule.GREATER_THAN_EQUAL, location.getBlockZ() - radius));

            // Maximum
            query.addCondition(new Condition(DataQueries.x, MatchRule.LESS_THAN_EQUAL, location.getBlockX() + radius));
            query.addCondition(new Condition(DataQueries.y, MatchRule.LESS_THAN_EQUAL, location.getBlockY() + radius));
            query.addCondition(new Condition(DataQueries.z, MatchRule.LESS_THAN_EQUAL, location.getBlockZ() + radius));
        }
    }
}
