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
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

public class ClickInventoryListener {
    /**
     * Saves event records when a player removes an item from an inventory.
     *
     * @param event Dispense event.
     */
    @Listener(order = Order.POST)
    public void onClickInventory(ClickInventoryEvent event, @First Player player) {
        //Make sure we have a transaction to validate
        if (event.getTransactions().size() <= 0) {
            return;
        }

        for(SlotTransaction transaction : event.getTransactions()) {
            Slot slot = transaction.getSlot();
            if (slot.parent() instanceof TileEntityCarrier && transaction.getOriginal() != transaction.getFinal()) {
                //If the final item is SOMETHING (or amount is more) person is inserting
                if (transaction.getFinal().getType() != ItemTypes.NONE || transaction.getFinal().getCount() > transaction.getOriginal().getCount()) {
                    if (Prism.listening.INSERT) {
                        PrismRecord.PrismRecordEventBuilder record = PrismRecord.create().source(event.getCause());
                        record.insertItem(transaction).save();
                    }
                }

                //If the final item is NONE (or amount is less) person is withdrawing
                if (transaction.getFinal().getType() == ItemTypes.NONE || transaction.getFinal().getCount() < transaction.getOriginal().getCount()) {
                    if (Prism.listening.REMOVE) {
                        PrismRecord.PrismRecordEventBuilder record = PrismRecord.create().source(event.getCause());
                        record.removeItem(transaction).save();
                    }
                }
            }
        }
    }
}
