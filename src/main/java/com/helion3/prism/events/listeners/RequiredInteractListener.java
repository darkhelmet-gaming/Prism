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
package com.helion3.prism.events.listeners;

import java.util.List;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent.Primary;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.Conditions;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.results.ResultRecord;
import com.helion3.prism.api.results.ResultRecordAggregate;
import com.helion3.prism.utils.DataQueries;
import com.helion3.prism.utils.Format;

public class RequiredInteractListener {
    /**
     * Listens for interactions by Players with active inspection wands.
     *
     * This listener is required and does not track any events.
     *
     * @param event InteractEvent
     */
    @Listener
    public void onInteract(final InteractEvent event) {
        Optional<Player> playerCause = event.getCause().first(Player.class);

        // Wand support
        if (playerCause.isPresent() && Prism.getActiveWands().contains(playerCause.get())) {
            if (event instanceof InteractBlockEvent) {
                final Player player = playerCause.get();

                QuerySession session = new QuerySession(player);
                Query query = session.newQuery();

                InteractBlockEvent blockEvent = (InteractBlockEvent) event;

                // Location of block
                Location<World> location = blockEvent.getTargetBlock().getLocation().get();

                // Primary click gets location relative to side clicked
                if (event instanceof Primary) {
                    location = location.getRelative(blockEvent.getTargetSide());
                }

                query.addConditions(Conditions.from(location));

                // Query the database asynchronously
                Prism.getGame().getScheduler().createTaskBuilder().async().execute(new Runnable(){
                    @Override
                    public void run(){
                        try {
                            // Iterate query results
                            List<ResultRecord> results = Prism.getStorageAdapter().records().query(session);
                            if (results.isEmpty()) {
                                // @todo move to language files
                                player.sendMessage(Format.error(Texts.of("Nothing found. See /pr ? for help.")));
                            } else {
                                for (ResultRecord result : results) {
                                    // Aggregate data
                                    int count = 1;
                                    if (result instanceof ResultRecordAggregate) {
                                        count = result.data.getInt(DataQueries.Count).get();
                                    }

                                    player.sendMessage(Texts.of(
                                        TextColors.DARK_AQUA, result.getSourceName(), " ",
                                        TextColors.WHITE, result.getEventVerb(), " ",
                                        TextColors.DARK_AQUA, result.getTargetName(), " ",
                                        (count > 1 ? "x" + count : ""),
                                        TextColors.WHITE, result.getRelativeTime()
                                    ));
                                }
                            }
                        } catch (Exception e) {
                            // @todo handle
                            e.printStackTrace();
                        }
                    }
                }).submit(Prism.getPlugin());
            }
        }
    }
}
