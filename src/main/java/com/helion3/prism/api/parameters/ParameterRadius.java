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
package com.helion3.prism.api.parameters;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.entity.living.player.Player;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.collect.ImmutableList;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.Format;

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
        return (source instanceof Player);
    }

    @Override
    public boolean acceptsValue(String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public Optional<CompletableFuture<?>> process(QuerySession session, String parameter, String value, Query query) {
        if (session.getCommandSource() instanceof Player) {
            Player player = (Player) session.getCommandSource();
            Location<World> location = player.getLocation();

            int radius = Integer.parseInt(value);
            int maxRadius = Prism.getConfig().getNode("limits", "radius", "max").getInt();

            // Enforce max radius unless player has override perms
            if (radius > maxRadius && !player.hasPermission("prism.override.radius")) {
                // @todo move this
                player.sendMessage(Format.subduedHeading(String.format("Limiting radius to maximum of %d", maxRadius)));
                radius = maxRadius;
            }

            session.setRadius(radius);

            query.addCondition(ConditionGroup.from(location, radius));
        }

        return Optional.empty();
    }

    @Override
    public Optional<Pair<String, String>> processDefault(QuerySession session, Query query) {
        if (session.getCommandSource() instanceof Player) {
            // Default radius from config
            int defaultRadius = Prism.getConfig().getNode("defaults", "radius").getInt();

            // Player location
            Location<World> location = ((Player) session.getCommandSource()).getLocation();

            query.addCondition(ConditionGroup.from(location, defaultRadius));

            session.setRadius(defaultRadius);

            return Optional.of(Pair.of(aliases.get(0), "" + defaultRadius));
        }

        return Optional.empty();
    }
}
