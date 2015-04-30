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

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.service.event.EventManager;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.helion3.prism.api.parameters.ParameterEventName;
import com.helion3.prism.api.parameters.ParameterHandler;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.commands.PrismCommands;
import com.helion3.prism.events.listeners.BlockBreakListener;
import com.helion3.prism.events.listeners.PlayerJoinListener;
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
final public class Prism {

    private static Configuration config;
    private static List<ParameterHandler> handlers = new ArrayList<ParameterHandler>();
    private static Logger logger;
    private static StorageAdapter storageAdapter;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    /**
     * Performs bootstrapping of Prism resources/objects.
     *
     * @param event Server started
     */
    @Subscribe
    public void onServerStart(ServerStartedEvent event) {

        // Game reference
        Game game = event.getGame();

        // Load configuration file
        config = new Configuration(defaultConfig, configManager);

        // Register handlers
        registerParameterHandler(new ParameterEventName());

        // Listen to events
        registerSpongeEventListeners(game.getEventManager());

        // Initialize storage engine
        storageAdapter = new MongoStorageAdapter();
        try {
            storageAdapter.connect();
        } catch (Exception e) {
            // @todo handle this
            e.printStackTrace();
        }

        // Initialize the recording queue manager
        new RecordingQueueManager().start();

        // Commands
        game.getCommandDispatcher().register(this, PrismCommands.getCommand(game), "prism", "pr");

        logger.info("Prism started successfully. Bad guys beware.");
    }

    /**
     *
     * @param handler
     */
    public void registerParameterHandler(ParameterHandler handler) {
        checkNotNull(handler);
        // @todo validate alias doesn't exist
        handlers.add(handler);
    }

    /**
     * Returns the plugin configuration
     * @return Configuration
     */
    public static Configuration getConfig() {
        return config;
    }

    /**
     * Returns a specific handler for a given parameter
     * @param parameter String parameter name
     * @return
     */
    public static Optional<ParameterHandler> getHandlerForParameter(String parameter) {
        ParameterHandler result = null;
        for(ParameterHandler handler : Prism.getParameterHandlers()) {
            if (handler.handles(parameter)) {
                result = handler;
            }
        }
        return Optional.fromNullable(result);
    }

    /**
     * Returns the Logger instance for this plugin.
     * @return Logger instance
     */
    public static Logger getLogger() {
        return logger;
    }

    /**
     * Returns all currently registered parameter handlers.
     * @return List of {@link ParameterHandler}
     */
    public static List<ParameterHandler> getParameterHandlers() {
        return handlers;
    }

    /**
     * Returns our storage/database adapter.
     * @return Storage adapter.
     */
    public static StorageAdapter getStorageAdapter() {
        return storageAdapter;
    }

    /**
     * Injects the Logger instance for this plugin
     * @param log Logger
     */
    @Inject
    private void setLogger(Logger log) {
        logger = log;
    }

    /**
     * Register all event listeners.
     */
    private void registerSpongeEventListeners(EventManager eventManager) {

        // Block events
        eventManager.register(this, new BlockBreakListener());

        // Player events
        eventManager.register(this, new PlayerJoinListener());

    }
}