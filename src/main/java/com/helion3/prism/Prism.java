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
package com.helion3.prism;

import org.spongepowered.api.Game;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.service.event.EventManager;
import org.spongepowered.api.util.event.Subscribe;

import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.commands.PrismCommand;
import com.helion3.prism.events.listeners.BlockBreakListener;
import com.helion3.prism.queues.RecordingQueueManager;
import com.helion3.prism.storage.mongodb.MongoStorageAdapter;

/**
 * Prism is an event logging + rollback/restore engine for Minecraft
 * servers. 
 * 
 * @author viveleroi
 *
 */
@Plugin(id = "Prism", name = "Prism", version = "3.0")
public class Prism {

    private static StorageAdapter storageAdapter;

    /**
     * Performs bootstrapping of Prism resources/objects.
     *
     * @param event Server started
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {

        // Game reference
        Game game = event.getGame();

        // Listen to events
        registerSpongeEventListeners(game.getEventManager());

        // Initialize storage engine
        // @todo needs config
        storageAdapter = new MongoStorageAdapter();
        try {
            storageAdapter.connect();
        } catch (Exception e) {
            // @todo handle this
            e.printStackTrace();
        }

        // Initialize the recording queue manager
        new RecordingQueueManager().start();
        
        // Listen for commands
        CommandService cmdService = game.getCommandDispatcher();
        cmdService.register(this, new PrismCommand(), "prism", "pr");

    }

    /**
     * 
     * @return
     */
    public static StorageAdapter getStorageAdapter() {
        return storageAdapter;
    }

    /**
     * Register all event listeners.
     */
    private void registerSpongeEventListeners(EventManager eventManager) {

        // Block events
        eventManager.register(this, new BlockBreakListener());

    }
}