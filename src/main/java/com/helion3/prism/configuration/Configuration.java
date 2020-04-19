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

package com.helion3.prism.configuration;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.helion3.prism.Prism;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;

public class Configuration {

    private ConfigurationLoader<CommentedConfigurationNode> configurationLoader;
    private ObjectMapper<Config>.BoundInstance objectMapper;

    public Configuration(Path path) {
        try {
            this.configurationLoader = HoconConfigurationLoader.builder().setPath(path).build();
            this.objectMapper = ObjectMapper.forClass(Config.class).bindToNew();
        } catch (Exception ex) {
            Prism.getInstance().getLogger().error("Encountered an error while initializing configuration", ex);
        }
    }

    public void loadConfiguration() {
        try {
            ConfigurationNode configurationNode = getConfigurationLoader().load();

            if (!configurationNode.isVirtual() && configurationNode.getNode("general", "schema-version").isVirtual()) {
                Prism.getInstance().getLogger().info("Converting Configuration...");
                convertConfiguration(configurationNode);
            } else {
                getObjectMapper().populate(configurationNode);
            }

            Prism.getInstance().getLogger().info("Successfully loaded configuration file.");
        } catch (Exception ex) {
            Prism.getInstance().getLogger().error("Encountered an error while loading config", ex);
        }
    }

    public void saveConfiguration() {
        try {
            ConfigurationNode configurationNode = getConfigurationLoader().createEmptyNode();
            getObjectMapper().serialize(configurationNode);
            getConfigurationLoader().save(configurationNode);
            Prism.getInstance().getLogger().info("Successfully saved configuration file.");
        } catch (Exception ex) {
            Prism.getInstance().getLogger().error("Encountered an error while saving config", ex);
        }
    }

    private void convertConfiguration(ConfigurationNode configurationNode) {
        ConfigurationNode storage = configurationNode.getNode("storage");
        if (!storage.isVirtual()) {
            String engine = storage.getNode("engine").getString();
            getConfig().getStorageCategory().setExpireRecords(storage.getNode("expireRecords").getString("4w"));
            getConfig().getStorageCategory().setMaximumPoolSize(storage.getNode("maxPoolSize").getInt(10));
            getConfig().getStorageCategory().setMinimumIdle(storage.getNode("minPoolSize").getInt(2));
            getConfig().getStorageCategory().setPurgeBatchLimit(storage.getNode("purgeBatchLimit").getInt(100000));

            if (StringUtils.equalsIgnoreCase(engine, "h2")) {
                ConfigurationNode database = configurationNode.getNode("db", "h2");
                getConfig().getStorageCategory().setEngine("h2");
                getConfig().getStorageCategory().setTablePrefix(database.getNode("tablePrefix").getString("prism_"));
            } else if (StringUtils.equalsIgnoreCase(engine, "mongo")) {
                ConfigurationNode database = configurationNode.getNode("db", "mongo");
                getConfig().getStorageCategory().setAddress(database.getNode("host").getString("127.0.0.1") + ":" + database.getNode("port").getInt(27017));
                getConfig().getStorageCategory().setEngine("mongo");
                getConfig().getStorageCategory().setPassword(database.getNode("pass").getString());
                getConfig().getStorageCategory().setUsername(database.getNode("user").getString());
            } else if (StringUtils.equalsIgnoreCase(engine, "mysql")) {
                ConfigurationNode database = configurationNode.getNode("db", "mysql");
                getConfig().getStorageCategory().setAddress(database.getNode("host").getString("localhost") + ":" + database.getNode("port").getInt(3306));
                getConfig().getStorageCategory().setEngine("mysql");
                getConfig().getStorageCategory().setPassword(database.getNode("pass").getString());
                getConfig().getStorageCategory().setTablePrefix(database.getNode("tablePrefix").getString("prism_"));
                getConfig().getStorageCategory().setUsername(database.getNode("user").getString());
            }

            // Setting 'should-expire' is unnecessary because @Setting in StorageCategory.java does it automatically,
            // but put it anyway
            getConfig().getStorageCategory().setShouldExpire(configurationNode.getNode("should-expire").getBoolean());
            getConfig().getStorageCategory().setMysqlDriver(configurationNode.getNode("mysql-driver").getString());
            getConfig().getStorageCategory().setDatabase(configurationNode.getNode("db", "name").getString("prism"));
            getConfig().getGeneralCategory().setSchemaVersion(1);
        }

        ConfigurationNode defaults = configurationNode.getNode("defaults");
        if (!defaults.isVirtual()) {
            getConfig().getDefaultCategory().setEnabled(defaults.getNode("enabled").getBoolean(true));
            getConfig().getDefaultCategory().setRadius(defaults.getNode("radius").getInt(5));
            getConfig().getDefaultCategory().setSince(defaults.getNode("since").getString("3d"));
        }

        ConfigurationNode display = configurationNode.getNode("display", "dateFormat");
        if (!display.isVirtual()) {
            String dateFormat = display.getString("d/M/yy hh:mm:ss a");
            if (StringUtils.equals(dateFormat, "d/M/yy hh:mm:ss")) {
                getConfig().getGeneralCategory().setDateFormat("d/M/yy hh:mm:ss a"); // Adds AM/PM to 12-hour format
            } else {
                getConfig().getGeneralCategory().setDateFormat(dateFormat);
            }
        }

        ConfigurationNode events = configurationNode.getNode("events");
        if (!events.isVirtual()) {
            getConfig().getEventCategory().setBlockBreak(events.getNode("break").getBoolean(true));
            getConfig().getEventCategory().setBlockDecay(events.getNode("decay").getBoolean(true));
            getConfig().getEventCategory().setBlockGrow(events.getNode("grow").getBoolean(true));
            getConfig().getEventCategory().setBlockPlace(events.getNode("place").getBoolean(true));
            getConfig().getEventCategory().setSignChange(events.getNode("signchange").getBoolean(true));
            getConfig().getEventCategory().setCommandExecute(events.getNode("command").getBoolean(false));
            getConfig().getEventCategory().setEntityDeath(events.getNode("death").getBoolean(true));
            getConfig().getEventCategory().setInventoryClose(events.getNode("close").getBoolean(false));
            getConfig().getEventCategory().setInventoryOpen(events.getNode("open").getBoolean(false));
            getConfig().getEventCategory().setItemDrop(events.getNode("drop").getBoolean(false));
            getConfig().getEventCategory().setItemInsert(events.getNode("insert").getBoolean(false));
            getConfig().getEventCategory().setItemPickup(events.getNode("pickup").getBoolean(false));
            getConfig().getEventCategory().setItemRemove(events.getNode("remove").getBoolean(false));
            getConfig().getEventCategory().setPlayerDisconnect(events.getNode("quit").getBoolean(false));
            getConfig().getEventCategory().setPlayerJoin(events.getNode("join").getBoolean(false));
        }

        ConfigurationNode maxRadius = configurationNode.getNode("limits", "radius", "max");
        if (!maxRadius.isVirtual()) {
            getConfig().getLimitCategory().setMaximumRadius(maxRadius.getInt(100));
        }

        ConfigurationNode query = configurationNode.getNode("query");
        if (!query.isVirtual()) {
            getConfig().getLimitCategory().setMaximumActionable(query.getNode("actionable", "limit").getInt(10000));
            getConfig().getLimitCategory().setMaximumLookup(query.getNode("lookup", "limit").getInt(1000));
        }

        ConfigurationNode blacklist = configurationNode.getNode("blacklist");
        if (!blacklist.isVirtual()) {
            try {
                getConfig().getGeneralCategory().setBlacklist(blacklist.getList(TypeToken.of(String.class), Lists.newArrayList()));
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        }
    }

    private ConfigurationLoader<CommentedConfigurationNode> getConfigurationLoader() {
        return configurationLoader;
    }

    private ObjectMapper<Config>.BoundInstance getObjectMapper() {
        return objectMapper;
    }

    public Config getConfig() {
        return getObjectMapper().getInstance();
    }
}