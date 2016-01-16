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

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.GrowBlockEvent;
import org.spongepowered.api.event.world.ExplosionEvent.Detonate;

import com.helion3.prism.Prism;
import com.helion3.prism.api.records.PrismRecord;
import com.helion3.prism.api.records.PrismRecord.PrismRecordEventBuilder;

public class ChangeBlockListener {
    // @todo move these to ignore
    private final boolean hearBreak = Prism.getConfig().getNode("events", "block", "break").getBoolean();
    private final boolean hearDecay = Prism.getConfig().getNode("events", "block", "decay").getBoolean();
    private final boolean hearGrow = Prism.getConfig().getNode("events", "block", "grow").getBoolean();
    private final boolean hearPlace = Prism.getConfig().getNode("events", "block", "place").getBoolean();

    /**
     * Listens to the base change block event.
     *
     * @param event
     */
    @Listener
    public void onChangeBlock(final ChangeBlockEvent event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            PrismRecordEventBuilder record = PrismRecord.create().source(event.getCause());

            if (event instanceof ChangeBlockEvent.Break || event instanceof Detonate) {
                // Air blocks are listed in Detonate events
                if (hearBreak && !transaction.getOriginal().getState().getType().equals(BlockTypes.AIR)) {
                    record.brokeBlock(transaction).save();
                }
            }
            else if (event instanceof ChangeBlockEvent.Place) {
                if (hearPlace) {
                    record.placedBlock(transaction).save();
                }
            }
            else if (event instanceof ChangeBlockEvent.Decay) {
                if (hearDecay) {
                    record.decayedBlock(transaction).save();
                }
            }
            else if (event instanceof GrowBlockEvent) {
                if (hearGrow) {
                    record.grewBlock(transaction).save();
                }
            }
        }
    }
}