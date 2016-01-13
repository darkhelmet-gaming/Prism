package com.helion3.prism.events.listeners.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.helion3.prism.api.records.PrismRecord;

public class ChangeBlockBreakListener {
    /**
     * Listens to all block break events.
     *
     * @param event ChangeBlockEvent.Break
     */
    @Listener
    public void onPlaceBlock(final ChangeBlockEvent.Break event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            PrismRecord.create().source(event.getCause()).brokeBlock(transaction).save();
        }
    }
}
