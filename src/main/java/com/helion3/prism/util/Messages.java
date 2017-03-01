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
package com.helion3.prism.util;

import java.util.Optional;
import java.util.UUID;

import com.helion3.prism.api.records.ResultAggregate;
import com.helion3.prism.api.records.ResultComplete;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3i;
import com.helion3.prism.Prism;
import com.helion3.prism.api.records.Result;

public class Messages {
    private Messages() {}

    /**
     * Generates Text output from a Result.
     *
     * @param result Result
     * @return Text
     */
    public static Text from(Result result, boolean extended) {
        Builder builder = Text.builder().append(Text.of(
            TextColors.DARK_AQUA, result.getSourceName(), " ",
            TextColors.WHITE, result.getEventVerb(), " "
        ));

        String target = result.getTargetName();
        if (!target.isEmpty()) {
            builder.append(Text.of(TextColors.DARK_AQUA, target, " "));
        }

        if (result instanceof ResultAggregate) {
            int count = result.data.getInt(DataQueries.Count).get();
            builder.append(Text.of(TextColors.GREEN, "x" + count, " "));
        } else {
            ResultComplete recordComplete = (ResultComplete) result;
            Optional<Object> optionalLocation = result.data.get(DataQueries.Location);

            if (optionalLocation.isPresent()) {
                DataView loc = (DataView) optionalLocation.get();

                int x = loc.getInt(DataQueries.X).get();
                int y = loc.getInt(DataQueries.Y).get();
                int z = loc.getInt(DataQueries.Z).get();

                builder.append(Text.of(TextColors.GRAY, "(x:", x, " y:", y, " z:", z, ") "));

                UUID worldUuid;
                if (loc.get(DataQueries.WorldUuid).get() instanceof UUID) {
                    worldUuid = (UUID) loc.get(DataQueries.WorldUuid).get();
                } else {
                    worldUuid = UUID.fromString(loc.getString(DataQueries.WorldUuid).get());
                }

                // Allow teleportation on click
                Optional<World> world = Prism.getGame().getServer().getWorld(worldUuid);
                if (world.isPresent()) {
                    Location<World> teleportLoc = world.get().getLocation(new Vector3i(x, y, z));

                    builder.onClick(TextActions.executeCallback(t -> {
                        if (t instanceof Player) {
                            ((Player) t).setLocation(teleportLoc);
                        }
                    }));
                }
            }

            if (extended) {
                builder.append(Text.of(TextColors.WHITE, "@ ", recordComplete.getTime()));
            } else {
                builder.append(Text.of(TextColors.WHITE, recordComplete.getRelativeTime()));
            }
        }

        return builder.build();
    }
}
