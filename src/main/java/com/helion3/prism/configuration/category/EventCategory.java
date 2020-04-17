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

package com.helion3.prism.configuration.category;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class EventCategory {

    @Setting(value = "block-break", comment = "Log when blocks are broken")
    private boolean blockBreak = true;

    @Setting(value = "block-decay", comment = "Log when blocks decay")
    private boolean blockDecay = true;

    @Setting(value = "block-grow", comment = "Log when blocks grow")
    private boolean blockGrow = true;

    @Setting(value = "block-place", comment = "Log when blocks are placed")
    private boolean blockPlace = true;

    @Setting(value = "sign-change", comment = "Log when a sign is changed")
    private boolean signChange = true;

    @Setting(value = "command-execute", comment = "Log when commands are executed")
    private boolean commandExecute = false;

    @Setting(value = "entity-death", comment = "Log when living entities are destroyed")
    private boolean entityDeath = true;

    @Setting(value = "inventory-open", comment = "Log when inventories are opened")
    private boolean inventoryOpen = false;

    @Setting(value = "inventory-close", comment = "Log when inventories are closed")
    private boolean inventoryClose = false;

    @Setting(value = "item-drop", comment = "Log when items are dropped")
    private boolean itemDrop = false;

    @Setting(value = "item-insert", comment = "Log when items are added to inventories")
    private boolean itemInsert = false;

    @Setting(value = "item-pickup", comment = "Log when items are picked up")
    private boolean itemPickup = false;

    @Setting(value = "item-remove", comment = "Log when items are removed from inventories")
    private boolean itemRemove = false;

    @Setting(value = "player-disconnect", comment = "Log when players disconnect")
    private boolean playerDisconnect = false;

    @Setting(value = "player-join", comment = "Log when players join")
    private boolean playerJoin = false;

    public boolean isBlockBreak() {
        return blockBreak;
    }

    public void setBlockBreak(boolean blockBreak) {
        this.blockBreak = blockBreak;
    }

    public boolean isBlockDecay() {
        return blockDecay;
    }

    public void setBlockDecay(boolean blockDecay) {
        this.blockDecay = blockDecay;
    }

    public boolean isBlockGrow() {
        return blockGrow;
    }

    public void setBlockGrow(boolean blockGrow) {
        this.blockGrow = blockGrow;
    }

    public boolean isBlockPlace() {
        return blockPlace;
    }

    public void setBlockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }

    public boolean isSignChange() {
        return signChange;
    }

    public void setSignChange(boolean signChange) {
        this.signChange = signChange;
    }

    public boolean isCommandExecute() {
        return commandExecute;
    }

    public void setCommandExecute(boolean commandExecute) {
        this.commandExecute = commandExecute;
    }

    public boolean isEntityDeath() {
        return entityDeath;
    }

    public void setEntityDeath(boolean entityDeath) {
        this.entityDeath = entityDeath;
    }

    public boolean isInventoryOpen() {
        return inventoryOpen;
    }

    public void setInventoryOpen(boolean inventoryOpen) {
        this.inventoryOpen = inventoryOpen;
    }

    public boolean isInventoryClose() {
        return inventoryClose;
    }

    public void setInventoryClose(boolean inventoryClose) {
        this.inventoryClose = inventoryClose;
    }

    public boolean isItemDrop() {
        return itemDrop;
    }

    public void setItemDrop(boolean itemDrop) {
        this.itemDrop = itemDrop;
    }

    public boolean isItemInsert() {
        return itemInsert;
    }

    public void setItemInsert(boolean itemInsert) {
        this.itemInsert = itemInsert;
    }

    public boolean isItemPickup() {
        return itemPickup;
    }

    public void setItemPickup(boolean itemPickup) {
        this.itemPickup = itemPickup;
    }

    public boolean isItemRemove() {
        return itemRemove;
    }

    public void setItemRemove(boolean itemRemove) {
        this.itemRemove = itemRemove;
    }

    public boolean isPlayerDisconnect() {
        return playerDisconnect;
    }

    public void setPlayerDisconnect(boolean playerDisconnect) {
        this.playerDisconnect = playerDisconnect;
    }

    public boolean isPlayerJoin() {
        return playerJoin;
    }

    public void setPlayerJoin(boolean playerJoin) {
        this.playerJoin = playerJoin;
    }
}