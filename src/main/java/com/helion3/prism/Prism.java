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

package com.helion3.prism;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.helion3.prism.api.filters.FilterList;
import com.helion3.prism.api.filters.FilterMode;
import com.helion3.prism.api.flags.FlagClean;
import com.helion3.prism.api.flags.FlagDrain;
import com.helion3.prism.api.flags.FlagExtended;
import com.helion3.prism.api.flags.FlagHandler;
import com.helion3.prism.api.flags.FlagNoGroup;
import com.helion3.prism.api.flags.FlagOrder;
import com.helion3.prism.api.parameters.ParameterBlock;
import com.helion3.prism.api.parameters.ParameterCause;
import com.helion3.prism.api.parameters.ParameterEventName;
import com.helion3.prism.api.parameters.ParameterHandler;
import com.helion3.prism.api.parameters.ParameterPlayer;
import com.helion3.prism.api.parameters.ParameterRadius;
import com.helion3.prism.api.parameters.ParameterTime;
import com.helion3.prism.api.records.ActionableResult;
import com.helion3.prism.api.records.BlockResult;
import com.helion3.prism.api.records.EntityResult;
import com.helion3.prism.api.records.Result;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.commands.PrismCommands;
import com.helion3.prism.listeners.ChangeBlockListener;
import com.helion3.prism.listeners.ChangeInventoryListener;
import com.helion3.prism.listeners.DeathListener;
import com.helion3.prism.listeners.DropItemListener;
import com.helion3.prism.listeners.JoinListener;
import com.helion3.prism.listeners.QuitListener;
import com.helion3.prism.listeners.RequiredInteractListener;
import com.helion3.prism.queues.RecordingQueueManager;
import com.helion3.prism.storage.h2.H2StorageAdapter;
import com.helion3.prism.storage.mongodb.MongoStorageAdapter;
import com.helion3.prism.storage.mysql.MySQLStorageAdapter;
import com.helion3.prism.util.Reference;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * Prism is an event logging + rollback/restore engine for Minecraft servers.
 *
 * @author viveleroi
 */
@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        authors = Reference.AUTHORS,
        url = Reference.WEBSITE
)
public final class Prism {
    
    private static Prism instance;
    
    @Inject
    private PluginContainer pluginContainer;
    
    @Inject
    private Logger logger;
    
    @Inject
    @DefaultConfig(sharedRoot = false)
    private Path path;
    
    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
    
    private Configuration configuration;
    private Listening listening;
    private StorageAdapter storageAdapter;
    
    private final Set<UUID> activeWands = Sets.newHashSet();
    private final FilterList filterList = new FilterList(FilterMode.BLACKLIST);
    private final Set<FlagHandler> flagHandlers = Sets.newHashSet();
    private final Map<UUID, List<ActionableResult>> lastActionResults = Maps.newHashMap();
    private final Set<ParameterHandler> parameterHandlers = Sets.newHashSet();
    private final Map<String, Class<? extends Result>> resultRecords = Maps.newHashMap();
    
    @Listener
    public void onConstruction(GameConstructionEvent event) {
        instance = this;
    }
    
    @Listener
    public void onPreInitialization(GamePreInitializationEvent event) {
        configuration = new Configuration(path.toFile(), configurationLoader);
        listening = new Listening();
    }
    
    @Listener
    public void onInitialization(GameInitializationEvent event) {
        // Register FlagHandlers
        registerFlagHandler(new FlagClean());
        registerFlagHandler(new FlagDrain());
        registerFlagHandler(new FlagExtended());
        registerFlagHandler(new FlagNoGroup());
        registerFlagHandler(new FlagOrder());
        
        // Register ParameterHandlers
        registerParameterHandler(new ParameterBlock());
        registerParameterHandler(new ParameterCause());
        registerParameterHandler(new ParameterEventName());
        registerParameterHandler(new ParameterPlayer());
        registerParameterHandler(new ParameterRadius());
        registerParameterHandler(new ParameterTime());
        
        // Register ResultRecords
        registerResultRecord("break", BlockResult.class);
        registerResultRecord("decay", BlockResult.class);
        registerResultRecord("grow", BlockResult.class);
        registerResultRecord("place", BlockResult.class);
        registerResultRecord("death", EntityResult.class);
        
        // Register Commands
        Sponge.getCommandManager().register(this, PrismCommands.getCommand(), Reference.ID, "pr");
        
        // Register Listeners
        Sponge.getEventManager().registerListeners(getPluginContainer(), new ChangeBlockListener());
        
        if (getListening().DEATH) {
            Sponge.getEventManager().registerListeners(getPluginContainer(), new DeathListener());
        }
        
        if (getListening().DROP) {
            Sponge.getEventManager().registerListeners(getPluginContainer(), new DropItemListener());
        }
        
        if (getListening().JOIN) {
            Sponge.getEventManager().registerListeners(getPluginContainer(), new JoinListener());
        }
        
        if (getListening().PICKUP) {
            Sponge.getEventManager().registerListeners(getPluginContainer(), new ChangeInventoryListener());
        }
        
        if (getListening().QUIT) {
            Sponge.getEventManager().registerListeners(getPluginContainer(), new QuitListener());
        }
        
        // Events required for internal operation
        Sponge.getEventManager().registerListeners(getPluginContainer(), new RequiredInteractListener());
    }
    
    @Listener
    public void onPostInitialization(GamePostInitializationEvent event) {
    }
    
    @Listener
    public void onStartedServer(GameStartedServerEvent event) {
        String engine = getConfiguration().getNode("storage", "engine").getString();
        try {
            if (StringUtils.equalsIgnoreCase(engine, "h2")) {
                storageAdapter = new H2StorageAdapter();
            } else if (StringUtils.equalsIgnoreCase(engine, "mongo")) {
                storageAdapter = new MongoStorageAdapter();
            } else if (StringUtils.equalsIgnoreCase(engine, "mysql")) {
                storageAdapter = new MySQLStorageAdapter();
            } else {
                throw new Exception("Invalid storage engine configured.");
            }
            
            Preconditions.checkArgument(getStorageAdapter().connect());
            
            // Initialize the recording queue manager
            Task.builder()
                    .async()
                    .name("PrismRecordingQueueManager")
                    .interval(1, TimeUnit.SECONDS)
                    .execute(new RecordingQueueManager())
                    .submit(getPluginContainer());
            getLogger().info("Prism started successfully. Bad guys beware.");
        } catch (Exception ex) {
            Sponge.getEventManager().unregisterPluginListeners(getPluginContainer());
            getLogger().error("Encountered an error processing {}::onStartedServer", "Prism", ex);
        }
    }
    
    public static Prism getInstance() {
        return instance;
    }
    
    public PluginContainer getPluginContainer() {
        return pluginContainer;
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Path getPath() {
        return path;
    }
    
    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Listening getListening() {
        return listening;
    }
    
    public StorageAdapter getStorageAdapter() {
        return storageAdapter;
    }
    
    /**
     * Returns a list of players who have active inspection wands.
     *
     * @return A list of players' UUIDs who have an active inspection wand
     */
    public Set<UUID> getActiveWands() {
        return activeWands;
    }
    
    /**
     * Returns the blacklist manager.
     *
     * @return Blacklist
     */
    public FilterList getFilterList() {
        return filterList;
    }
    
    /**
     * Returns all currently registered flag handlers.
     *
     * @return List of {@link FlagHandler}
     */
    public Set<FlagHandler> getFlagHandlers() {
        return flagHandlers;
    }
    
    /**
     * Returns a specific handler for a given parameter
     *
     * @param flag {@link String} flag name
     * @return The {@link FlagHandler}, or empty if unsupported
     */
    public Optional<FlagHandler> getFlagHandler(String flag) {
        for (FlagHandler flagHandler : getFlagHandlers()) {
            if (flagHandler.handles(flag)) {
                return Optional.of(flagHandler);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Register a flag handler.
     *
     * @param flagHandler {@link FlagHandler}
     * @return True if the {@link FlagHandler} was registered
     */
    public boolean registerFlagHandler(FlagHandler flagHandler) {
        Preconditions.checkNotNull(flagHandler);
        return getFlagHandlers().add(flagHandler);
    }
    
    /**
     * Get a map of players and their last available actionable results.
     *
     * @return A map of players' UUIDs to a list of their {@link ActionableResult}s
     */
    public Map<UUID, List<ActionableResult>> getLastActionResults() {
        return lastActionResults;
    }
    
    /**
     * Returns all currently registered parameter handlers.
     *
     * @return List of {@link ParameterHandler}
     */
    public Set<ParameterHandler> getParameterHandlers() {
        return parameterHandlers;
    }
    
    /**
     * Returns a specific handler for a given parameter
     *
     * @param alias {@link String} parameter name
     * @return The {@link ParameterHandler}, or empty if unsupported
     */
    public Optional<ParameterHandler> getParameterHandler(String alias) {
        for (ParameterHandler parameterHandler : getParameterHandlers()) {
            if (parameterHandler.handles(alias)) {
                return Optional.of(parameterHandler);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Register a parameter handler.
     *
     * @param parameterHandler {@link ParameterHandler}
     * @return True if the {@link ParameterHandler} was registered
     */
    public boolean registerParameterHandler(ParameterHandler parameterHandler) {
        Preconditions.checkNotNull(parameterHandler);
        return getParameterHandlers().add(parameterHandler);
    }
    
    /**
     * Returns all currently registered result records.
     *
     * @return Map of event names to their {@link Result} class
     */
    private Map<String, Class<? extends Result>> getResultRecords() {
        return resultRecords;
    }
    
    /**
     * Returns the result record class for a given event
     *
     * @param event event name
     * @return {@link Result} Record class, or null if unsupported
     */
    public Class<? extends Result> getResultRecord(String event) {
        return getResultRecords().get(event);
    }
    
    /**
     * Register a custom result record for a given event name.
     *
     * @param event        {@link String} event name
     * @param resultRecord {@link Result} Record class
     * @return True if the {@link Result} Record class was registered
     */
    public boolean registerResultRecord(String event, Class<? extends Result> resultRecord) {
        Preconditions.checkNotNull(event);
        Preconditions.checkNotNull(resultRecord);
        return getResultRecords().putIfAbsent(event, resultRecord) == null;
    }
}