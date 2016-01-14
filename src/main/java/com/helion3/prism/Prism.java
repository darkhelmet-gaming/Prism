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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.config.DefaultConfig;

import com.google.inject.Inject;
import com.helion3.prism.api.parameters.ParameterEventName;
import com.helion3.prism.api.parameters.ParameterHandler;
import com.helion3.prism.api.parameters.ParameterPlayer;
import com.helion3.prism.api.parameters.ParameterRadius;
import com.helion3.prism.api.parameters.ParameterTime;
import com.helion3.prism.api.results.ActionableResult;
import com.helion3.prism.api.results.BlockChangeResultRecord;
import com.helion3.prism.api.results.ResultRecord;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.commands.PrismCommands;
import com.helion3.prism.events.listeners.DeathListener;
import com.helion3.prism.events.listeners.RequiredInteractListener;
import com.helion3.prism.events.listeners.JoinListener;
import com.helion3.prism.events.listeners.QuitListener;
import com.helion3.prism.events.listeners.RequiredJoinListener;
import com.helion3.prism.events.listeners.block.ChangeBlockBreakListener;
import com.helion3.prism.events.listeners.block.ChangeBlockDecayListener;
import com.helion3.prism.events.listeners.block.ChangeBlockGrowListener;
import com.helion3.prism.events.listeners.block.ChangeBlockPlaceListener;
import com.helion3.prism.queues.RecordingQueueManager;
import com.helion3.prism.storage.mongodb.MongoStorageAdapter;

/**
 * Prism is an event logging + rollback/restore engine for Minecraft servers.
 *
 * @author viveleroi
 *
 */
@Plugin(id = "Prism", name = "Prism", version = "3.0")
final public class Prism {
    private static List<Player> activeWands = new ArrayList<Player>();
    private static Configuration config;
    private static Game game;
    private static List<ParameterHandler> handlers = new ArrayList<ParameterHandler>();
    private static Map<Player, List<ActionableResult>> lastActionResults = new HashMap<Player, List<ActionableResult>>();
    private static Logger logger;
    private static Map<String,Class<? extends ResultRecord>> resultRecords = new HashMap<String,Class<? extends ResultRecord>>();
    private static Object plugin;
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
    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        plugin = this;

        // Load configuration file
        config = new Configuration(defaultConfig, configManager);

        // Register all result record classes
        registerEventResultRecords();

        // Register handlers
        registerParameterHandlers();

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
        game.getCommandManager().register(this, PrismCommands.getCommand(), "prism", "pr");

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
     * Returns a list of players who have active inspection wands.
     *
     * @return List of Players.
     */
    public static List<Player> getActiveWands() {
        return activeWands;
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

        return Optional.of(result);
    }

    /**
     * Returns the current game
     * @return Game
     */
    public static Game getGame() {
        return game;
    }

    /**
     * Injected Game instance.
     * @param injectGame Game
     */
    @Inject
    public void setGame(Game injectGame) {
        game = injectGame;
    }

    /**
     * Get a map of players and their last available actionable results.
     * @return
     */
    public static Map<Player, List<ActionableResult>> getLastActionResults() {
        return lastActionResults;
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
     *
     * @return
     */
    public static Object getPlugin() {
        return plugin;
    }

    /**
     * Returns the result record for a given event.
     * @param eventName Event name.
     * @return Result record class.
     */
    public static Class<? extends ResultRecord> getResultRecord(String eventName) {
        return resultRecords.get(eventName);
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
     * Registers all default event names and their handling classes
     */
    private void registerEventResultRecords() {
        registerResultRecord("block-break", BlockChangeResultRecord.class);
        registerResultRecord("block-decay", BlockChangeResultRecord.class);
        registerResultRecord("block-grow", BlockChangeResultRecord.class);
        registerResultRecord("block-place", BlockChangeResultRecord.class);
    }

    /**
     * Registers all default parameter handlers
     */
    private void registerParameterHandlers() {
        registerParameterHandler(new ParameterEventName());
        registerParameterHandler(new ParameterPlayer());
        registerParameterHandler(new ParameterRadius());
        registerParameterHandler(new ParameterTime());
    }

    /**
     * Register a custom result record for a given event name.
     * @param eventName
     * @param record
     */
    public void registerResultRecord(String eventName, Class<? extends ResultRecord> clazz) {
        if (resultRecords.containsKey(eventName)) {
            throw new IllegalArgumentException("A result record is already registered for event \"" + eventName + "\"");
        }

        resultRecords.put(eventName, clazz);
    }

    /**
     * Register all event listeners.
     */
    private void registerSpongeEventListeners(EventManager eventManager) {
        eventManager.unregisterPluginListeners(this);

        if (config.getNode("events", "block", "break").getBoolean()) {
            eventManager.registerListeners(this, new ChangeBlockBreakListener());
        }

        if (config.getNode("events", "block", "decay").getBoolean()) {
            eventManager.registerListeners(this, new ChangeBlockDecayListener());
        }

        if (config.getNode("events", "block", "grow").getBoolean()) {
            eventManager.registerListeners(this, new ChangeBlockGrowListener());
        }

        if (config.getNode("events", "block", "place").getBoolean()) {
            eventManager.registerListeners(this, new ChangeBlockPlaceListener());
        }

        if (config.getNode("events", "entity", "death").getBoolean()) {
            eventManager.registerListeners(this, new DeathListener());
        }

        if (config.getNode("events", "player", "join").getBoolean()) {
            eventManager.registerListeners(this, new JoinListener());
        }

        if (config.getNode("events", "player", "quit").getBoolean()) {
            eventManager.registerListeners(this, new QuitListener());
        }

        // Events required for internal operation
        eventManager.registerListeners(this, new RequiredJoinListener());
        eventManager.registerListeners(this, new RequiredInteractListener());
    }
}