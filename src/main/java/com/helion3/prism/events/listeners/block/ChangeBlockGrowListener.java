package com.helion3.prism.events.listeners.block;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;

import com.helion3.prism.api.records.PrismRecord;

public class ChangeBlockGrowListener {
    /**
     * Listens to all block grow events.
     *
     * @param event ChangeBlockEvent.Grow
     */
    @Listener
    public void onPlaceBlock(final ChangeBlockEvent.Grow event) {
        for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
            PrismRecord.create().source(event.getCause()).grewBlock(transaction).save();
        }
    }
}
