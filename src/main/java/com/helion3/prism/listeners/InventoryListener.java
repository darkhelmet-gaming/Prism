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
import com.helion3.prism.util.PrismEvents;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.Item;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ChangeInventoryEvent;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.item.inventory.DropItemEvent;
import org.spongepowered.api.event.item.inventory.InteractInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Carrier;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.CarriedInventory;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Locatable;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class InventoryListener {

    /**
     * Saves event records when a player interacts with an inventory.
     *
     * @param event  ClickInventoryEvent
     * @param player Player
     */
    @Listener(order = Order.POST)
    public void onClickInventory(ClickInventoryEvent event, @Root Player player) {
        if (event.getTransactions().isEmpty()
                || (!Prism.getInstance().getConfig().getEventCategory().isItemInsert()
                && !Prism.getInstance().getConfig().getEventCategory().isItemRemove())) {
            return;
        }

        for (SlotTransaction transaction : event.getTransactions()) {
            if (!(transaction.getSlot().parent() instanceof CarriedInventory)) {
                continue;
            }

            CarriedInventory<? extends Carrier> carriedInventory = (CarriedInventory<? extends Carrier>) transaction.getSlot().parent();
            if (carriedInventory.getCarrier().filter(Player.class::isInstance).isPresent()) {
                return;
            }

            // Get the location of the inventory otherwise fallback on the players location
            Location<World> location = carriedInventory.getCarrier()
                    .filter(Locatable.class::isInstance)
                    .map(Locatable.class::cast)
                    .map(Locatable::getLocation)
                    .orElse(player.getLocation());

            // Get the title of the inventory otherwise fallback on the class name
            String title = carriedInventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME)
                    .map(InventoryTitle::getValue)
                    .map(Text::toPlain)
                    .orElse(carriedInventory.getClass().getSimpleName());

            int capacity = carriedInventory.first().capacity();
            int index = transaction.getSlot().getInventoryProperty(SlotIndex.class).map(SlotIndex::getValue).orElse(-1);
            if (index < 0 || index >= capacity) {
                continue;
            }

            // Nothing happened - Player clicked an empty slot
            if (transaction.getOriginal().getType() == ItemTypes.NONE && transaction.getFinal().getType() == ItemTypes.NONE) {
                continue;
            }

            PrismRecord.EventBuilder eventBuilder = PrismRecord.create()
                    .source(event.getCause())
                    .container(title)
                    .location(location);


            if (transaction.getOriginal().getType() == transaction.getFinal().getType()) {

                // Remove - Splitting stack
                if (transaction.getOriginal().getQuantity() > transaction.getFinal().getQuantity()) {
                    if (!Prism.getInstance().getConfig().getEventCategory().isItemRemove()) {
                        continue;
                    }

                    Prism.getInstance().getLogger().debug("Item Remove - {} x{}",
                            transaction.getOriginal().getType().getId(), transaction.getOriginal().getQuantity() - transaction.getFinal().getQuantity());

                    eventBuilder
                            .event(PrismEvents.ITEM_INSERT)
                            .itemStack(transaction.getOriginal(), transaction.getOriginal().getQuantity() - transaction.getFinal().getQuantity())
                            .buildAndSave();

                    continue;
                }

                // Insert - Existing stack
                if (transaction.getOriginal().getQuantity() < transaction.getFinal().getQuantity()) {
                    if (!Prism.getInstance().getConfig().getEventCategory().isItemInsert()) {
                        continue;
                    }

                    Prism.getInstance().getLogger().debug("Item Insert - {} x{}",
                            transaction.getFinal().getType().getId(), transaction.getFinal().getQuantity() - transaction.getOriginal().getQuantity());

                    eventBuilder
                            .event(PrismEvents.ITEM_REMOVE)
                            .itemStack(transaction.getFinal(), transaction.getFinal().getQuantity() - transaction.getOriginal().getQuantity())
                            .buildAndSave();

                    continue;
                }
            }

            // Remove
            if (transaction.getOriginal().getType() != ItemTypes.NONE) {
                if (!Prism.getInstance().getConfig().getEventCategory().isItemRemove()) {
                    continue;
                }

                Prism.getInstance().getLogger().debug("Item Remove - {} x{}",
                        transaction.getOriginal().getType().getId(), transaction.getOriginal().getQuantity() - transaction.getFinal().getQuantity());

                eventBuilder
                        .event(PrismEvents.ITEM_REMOVE)
                        .itemStack(transaction.getOriginal(), transaction.getOriginal().getQuantity() - transaction.getFinal().getQuantity())
                        .buildAndSave();

                continue;
            }

            // Insert
            if (transaction.getOriginal().getType() == ItemTypes.NONE) {
                if (!Prism.getInstance().getConfig().getEventCategory().isItemInsert()) {
                    continue;
                }

                Prism.getInstance().getLogger().debug("Item Insert - {} x{}",
                        transaction.getFinal().getType().getId(), transaction.getFinal().getQuantity() - transaction.getOriginal().getQuantity());

                eventBuilder.event(PrismEvents.ITEM_INSERT)
                        .itemStack(transaction.getFinal(), transaction.getFinal().getQuantity() - transaction.getOriginal().getQuantity())
                        .buildAndSave();

                continue;
            }

            Prism.getInstance().getLogger().warn("Failed to handle ClickInventoryEvent");
        }
    }

    /**
     * Saves event records when a player picks up an item off of the ground.
     *
     * @param event  ChangeInventoryEvent.Pickup
     * @param player Player
     */
    @Listener(order = Order.POST)
    public void onChangeInventoryPickup(ChangeInventoryEvent.Pickup event, @Root Player player) {
        if (event.getTransactions().isEmpty() || !Prism.getInstance().getConfig().getEventCategory().isItemPickup()) {
            return;
        }

        for (SlotTransaction transaction : event.getTransactions()) {
            ItemStackSnapshot itemStack = transaction.getFinal();
            int quantity = itemStack.getQuantity();
            if (transaction.getOriginal().getType() != ItemTypes.NONE) {
                quantity -= transaction.getOriginal().getQuantity();
            }

            Prism.getInstance().getLogger().debug("Inventory pickup - {} x{}", itemStack.getType().getId(), quantity);

            PrismRecord.create()
                    .source(event.getCause())
                    .event(PrismEvents.ITEM_PICKUP)
                    .itemStack(itemStack, quantity)
                    .location(player.getLocation())
                    .buildAndSave();
        }
    }

    /**
     * Saves event records when a player drops an item on to the ground.
     *
     * @param event  DropItemEvent.Dispense
     * @param player Player
     */
    @Listener(order = Order.POST)
    public void onDropItemDispense(DropItemEvent.Dispense event, @Root Player player) {
        if (event.getEntities().isEmpty() || !Prism.getInstance().getConfig().getEventCategory().isItemDrop()) {
            return;
        }

        for (Entity entity : event.getEntities()) {
            if (!(entity instanceof Item)) {
                continue;
            }

            Item item = (Item) entity;
            if (!item.item().exists()) {
                continue;
            }

            ItemStackSnapshot itemStack = item.item().get();
            Prism.getInstance().getLogger().debug("Inventory dropped - {} x{}", itemStack.getType().getId(), itemStack.getQuantity());

            PrismRecord.create()
                    .source(event.getCause())
                    .event(PrismEvents.ITEM_DROP)
                    .itemStack(itemStack)
                    .location(player.getLocation())
                    .buildAndSave();
        }
    }

    /**
     * Saves event records when a player closes or opens an Inventory.
     *
     * @param event  InteractInventoryEvent
     * @param player Player
     */
    @Listener(order = Order.POST)
    public void onInteractInventory(InteractInventoryEvent event, @Root Player player) {
        if (!(event.getTargetInventory() instanceof CarriedInventory)
                || (!Prism.getInstance().getConfig().getEventCategory().isInventoryClose() && !Prism.getInstance().getConfig().getEventCategory().isInventoryOpen())) {
            return;
        }

        CarriedInventory<? extends Carrier> carriedInventory = (CarriedInventory<? extends Carrier>) event.getTargetInventory();
        if (carriedInventory.getCarrier().filter(Player.class::isInstance).isPresent()) {
            return;
        }

        // Get the location of the inventory otherwise fallback on the players location
        Location<World> location = carriedInventory.getCarrier()
                .filter(Locatable.class::isInstance)
                .map(Locatable.class::cast)
                .map(Locatable::getLocation)
                .orElse(player.getLocation());

        // Get the title of the inventory otherwise fallback on the class name
        String title = carriedInventory.getProperty(InventoryTitle.class, InventoryTitle.PROPERTY_NAME)
                .map(InventoryTitle::getValue)
                .map(Text::toPlain)
                .orElse(carriedInventory.getClass().getSimpleName());

        PrismRecord.EventBuilder eventBuilder = PrismRecord.create()
                .source(event.getCause())
                .container(title)
                .location(location);

        if (event instanceof InteractInventoryEvent.Close && Prism.getInstance().getConfig().getEventCategory().isInventoryClose()) {
            eventBuilder.event(PrismEvents.INVENTORY_CLOSE).buildAndSave();
        } else if (event instanceof InteractInventoryEvent.Open && Prism.getInstance().getConfig().getEventCategory().isInventoryOpen()) {
            eventBuilder.event(PrismEvents.INVENTORY_OPEN).buildAndSave();
        }
    }
}