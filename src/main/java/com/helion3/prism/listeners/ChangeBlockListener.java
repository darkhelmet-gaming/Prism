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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.helion3.prism.Prism;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecord.PrismRecordEventBuilder;
import com.helion3.prism.util.BlockUtil;
import com.helion3.prism.util.EventUtil;

public class ChangeBlockListener {
    /**
     * Listens to the base change block event.
     *
     * @param event
     */
    @Listener
    public void onChangeBlock(final ChangeBlockEvent event) {
        Optional<Player> playerCause = event.getCause().first(Player.class);
        if (playerCause.isPresent() && Prism.getActiveWands().contains(playerCause.get().getUniqueId())) {
            // Cancel and exit event here, not supposed to place/track a block with an active wand.
            event.setCancelled(true);
            return;
        }
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            if (!transaction.isValid()) {
                continue;
            }

            PrismRecordEventBuilder record = PrismRecord.create().source(event.getCause());

            BlockType original = transaction.getOriginal().getState().getType();
            BlockType finalBlock = transaction.getFinal().getState().getType();

            if (event instanceof ChangeBlockEvent.Break) {
                if (Prism.listening.BREAK &&
                        !BlockUtil.rejectBreakCombination(original, finalBlock) &&
                        !EventUtil.rejectBreakEventIdentity(original, finalBlock, event.getCause())) {
                    record.brokeBlock(transaction).save();
                }
            }
            else if (event instanceof ChangeBlockEvent.Place) {
                if (Prism.listening.PLACE &&
                        !BlockUtil.rejectPlaceCombination(original, finalBlock) &&
                        !EventUtil.rejectPlaceEventIdentity(original, finalBlock, event.getCause())) {
                    record.placedBlock(transaction).save();
                }
            }
            else if (event instanceof ChangeBlockEvent.Decay) {
                if (Prism.listening.DECAY) {
                    record.decayedBlock(transaction).save();
                }
            }
        }
    }
}