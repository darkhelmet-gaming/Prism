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
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.util.BlockUtil;
import com.helion3.prism.util.EventUtil;
import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.block.ChangeBlockEvent;

public class ChangeBlockListener {

    /**
     * Listens to the base change block event.
     *
     * @param event ChangeBlockEvent
     */
    @Listener(order = Order.POST)
    public void onChangeBlock(ChangeBlockEvent event) {
        if (event.getCause().first(Player.class).map(Player::getUniqueId).map(Prism.getInstance().getActiveWands()::contains).orElse(false)) {
            // Cancel and exit event here, not supposed to place/track a block with an active wand.
            event.setCancelled(true);
            return;
        }

        if (event.getTransactions().isEmpty()
                || (!Prism.getInstance().getConfig().getEventCategory().isBlockBreak()
                && !Prism.getInstance().getConfig().getEventCategory().isBlockDecay()
                && !Prism.getInstance().getConfig().getEventCategory().isBlockGrow()
                && !Prism.getInstance().getConfig().getEventCategory().isBlockPlace())) {
            return;
        }

        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!transaction.isValid() || !transaction.getOriginal().getLocation().isPresent()) {
                continue;
            }

            BlockType originalBlockType = transaction.getOriginal().getState().getType();
            BlockType finalBlockType = transaction.getFinal().getState().getType();

            PrismRecord.EventBuilder eventBuilder = PrismRecord.create()
                    .source(event.getCause())
                    .blockOriginal(transaction.getOriginal())
                    .blockReplacement(transaction.getFinal())
                    .location(transaction.getOriginal().getLocation().get());

            if (event instanceof ChangeBlockEvent.Break) {
                if (!Prism.getInstance().getConfig().getEventCategory().isBlockBreak()
                        || BlockUtil.rejectBreakCombination(originalBlockType, finalBlockType)
                        || EventUtil.rejectBreakEventIdentity(originalBlockType, finalBlockType, event.getCause())) {
                    continue;
                }

                eventBuilder
                        .event(PrismEvents.BLOCK_BREAK)
                        .target(originalBlockType.getId().replace("_", " "))
                        .buildAndSave();
            } else if (event instanceof ChangeBlockEvent.Decay) {
                if (!Prism.getInstance().getConfig().getEventCategory().isBlockDecay()) {
                    continue;
                }

                eventBuilder
                        .event(PrismEvents.BLOCK_DECAY)
                        .target(originalBlockType.getId().replace("_", " "))
                        .buildAndSave();
            } else if (event instanceof ChangeBlockEvent.Grow) {
                if (!Prism.getInstance().getConfig().getEventCategory().isBlockGrow()) {
                    continue;
                }

                eventBuilder
                        .event(PrismEvents.BLOCK_GROW)
                        .target(finalBlockType.getId().replace("_", " "))
                        .buildAndSave();
            } else if (event instanceof ChangeBlockEvent.Place) {
                if (!Prism.getInstance().getConfig().getEventCategory().isBlockGrow()
                        || BlockUtil.rejectPlaceCombination(originalBlockType, finalBlockType)
                        || EventUtil.rejectPlaceEventIdentity(originalBlockType, finalBlockType, event.getCause())) {
                    continue;
                }

                eventBuilder
                        .event(PrismEvents.BLOCK_PLACE)
                        .target(finalBlockType.getId().replace("_", " "))
                        .buildAndSave();
            }
        }
    }
}