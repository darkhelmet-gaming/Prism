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

import com.helion3.prism.Prism;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.AsyncUtil;
import com.helion3.prism.util.Format;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class RequiredInteractListener {

    /**
     * Listens for interactions by Players with active inspection wands.
     * <br>
     * This listener is required and does not track any events.
     *
     * @param event  InteractBlockEvent
     * @param player Player
     */
    @Listener(order = Order.EARLY)
    public void onInteractBlock(InteractBlockEvent event, @First Player player) {
        // Wand support
        if (!Prism.getInstance().getActiveWands().contains(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        // Ignore OffHand events
        if (event instanceof InteractBlockEvent.Primary.OffHand || event instanceof InteractBlockEvent.Secondary.OffHand) {
            return;
        }

        // Verify target block is valid
        if (event.getTargetBlock() == BlockSnapshot.NONE || !event.getTargetBlock().getLocation().isPresent()) {
            return;
        }

        // Location of block
        Location<World> location = event.getTargetBlock().getLocation().get();

        // Secondary click gets location relative to side clicked
        if (event instanceof InteractBlockEvent.Secondary) {
            location = location.getRelative(event.getTargetSide());
        }

        QuerySession session = new QuerySession(player);
        // session.addFlag(Flag.EXTENDED);
        session.addFlag(Flag.NO_GROUP);
        session.newQuery().addCondition(ConditionGroup.from(location));

        player.sendMessage(Text.of(
                Format.prefix(), TextColors.GOLD,
                "--- Inspecting ", Format.item(location.getBlockType().getId(), true),
                " at ", location.getBlockX(), " ", location.getBlockY(), " ", location.getBlockZ(), " ---"));

        // Pass off to an async lookup helper
        AsyncUtil.lookup(session);
    }

    /**
     * Listens for interactions by Players with active inspection wands.
     * <br>
     * This listener is required and does not track any events.
     *
     * @param event  InteractEntityEvent
     * @param player Player
     */
    @Listener(order = Order.EARLY)
    public void onInteractEntity(InteractEntityEvent event, @First Player player) {
        // Wand support
        if (!Prism.getInstance().getActiveWands().contains(player.getUniqueId())) {
            return;
        }

        event.setCancelled(true);

        // Ignore OffHand events
        if (event instanceof InteractEntityEvent.Primary.OffHand || event instanceof InteractEntityEvent.Secondary.OffHand) {
            return;
        }

        player.sendMessage(Format.error(Text.of("Cannot interact with entities while inspection is active!")));
    }
}