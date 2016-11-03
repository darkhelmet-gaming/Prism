package com.helion3.prism.events.listeners;

import com.helion3.prism.api.records.PrismRecord;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;

public class ChangeInventoryListener {
    /**
     * Saves event records when a player picks up an item.
     *
     * @param event Pickup event.
     */
    @Listener(order = Order.POST)
    public void onItemPickup(final ChangeInventoryEvent.Pickup event, @Root Player player) {
        PrismRecord.create().player(player).pickedUp(event.getTargetEntity()).save();
    }
}
