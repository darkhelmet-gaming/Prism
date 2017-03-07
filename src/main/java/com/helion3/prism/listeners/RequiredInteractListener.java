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
package com.helion3.prism.listeners;

import java.util.Optional;

import com.helion3.prism.api.flags.Flag;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent.Secondary;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.helion3.prism.Prism;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.AsyncUtil;
import com.helion3.prism.util.Format;

public class RequiredInteractListener {
    /**
     * Listens for interactions by Players with active inspection wands.
     *
     * This listener is required and does not track any events.
     *
     * @param event InteractEvent
     */
    @Listener
    public void onInteract(final InteractBlockEvent event, @First Player player) {
        // Wand support
        if (Prism.getActiveWands().contains(player.getUniqueId())) {
            QuerySession session = new QuerySession(player);
            session.addFlag(Flag.EXTENDED);
            session.addFlag(Flag.NO_GROUP);

            Query query = session.newQuery();

            if (event.getTargetBlock().equals(BlockSnapshot.NONE)) {
                return;
            }

            // Location of block
            Location<World> location = event.getTargetBlock().getLocation().get();

            // Secondary click gets location relative to side clicked
            if (event instanceof Secondary) {
                location = location.getRelative(event.getTargetSide());
            }

            query.addCondition(ConditionGroup.from(location));

            player.sendMessage(Format.heading(String.format("Querying x:%d y:%d z:%d:",
                    location.getBlockX(),
                    location.getBlockY(),
                    location.getBlockZ())));

            // Pass off to an async lookup helper
            try {
                AsyncUtil.lookup(session);
            } catch (Exception e) {
                player.sendMessage(Format.error(e.getMessage()));
                e.printStackTrace();
            }

            event.setCancelled(true);
        }
    }
}
