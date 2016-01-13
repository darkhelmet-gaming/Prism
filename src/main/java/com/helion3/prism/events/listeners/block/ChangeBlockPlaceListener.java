package com.helion3.prism.events.listeners.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.helion3.prism.api.records.PrismRecord;

public class ChangeBlockPlaceListener {
    /**
     * Listens to all block place events.
     *
     * @param event ChangeBlockEvent.Place
     */
    @Listener
    public void onPlaceBlock(final ChangeBlockEvent.Place event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            PrismRecord.create().source(event.getCause()).placedBlock(transaction).save();
        }
    }
}
