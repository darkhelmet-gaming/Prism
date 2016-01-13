package com.helion3.prism.events.listeners.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.helion3.prism.api.records.PrismRecord;

public class ChangeBlockDecayListener {
    /**
     * Listens to all block decay events.
     *
     * @param event ChangeBlockEvent.Decay
     */
    @Listener
    public void onPlaceBlock(final ChangeBlockEvent.Decay event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            PrismRecord.create().source(event.getCause()).decayedBlock(transaction).save();
        }
    }
}
