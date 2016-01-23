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

import java.io.File;
import java.io.IOException;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Configuration {

    private ConfigurationNode rootNode = null;

    /**
     * Loads (creates new if needed) Prism configuration file.
     * @param defaultConfig
     * @param configManager
     */
    public Configuration(File defaultConfig, ConfigurationLoader<CommentedConfigurationNode> configManager) {
        try {
            // If file does not exist, we must create it
            if (!defaultConfig.exists()) {
                defaultConfig.getParentFile().mkdirs();
                defaultConfig.createNewFile();
                rootNode = configManager.createEmptyNode(ConfigurationOptions.defaults());
                Prism.getLogger().info("Creating new config at mods/Prism/Prism.conf");
            } else {
                rootNode = configManager.load();
            }

            // Database
            ConfigurationNode dbName = rootNode.getNode("db", "name");
            if (dbName.isVirtual()) {
                dbName.setValue("prism");
            }

            ConfigurationNode dbTablePrefix = rootNode.getNode("db", "tablePrefix");
            if (dbTablePrefix.isVirtual()) {
                dbTablePrefix.setValue("prism");
            }

            ConfigurationNode dbMongoHost = rootNode.getNode("db", "mongo", "host");
            if (dbMongoHost.isVirtual()) {
                dbMongoHost.setValue("127.0.0.1");
            }

            ConfigurationNode dbMongoPort = rootNode.getNode("db", "mongo", "port");
            if (dbMongoPort.isVirtual()) {
                dbMongoPort.setValue(27017);
            }

            ConfigurationNode dbMongoExpires = rootNode.getNode("db", "mongo", "expiration");
            if (dbMongoExpires.isVirtual()) {
                dbMongoExpires.setValue("8w");
            }

            // Events
            ConfigurationNode eventBlockBreak = rootNode.getNode("events", "block", "break");
            if (eventBlockBreak.isVirtual()) {
                eventBlockBreak.setValue(true);
            }

            ConfigurationNode eventBlockDecay = rootNode.getNode("events", "block", "decay");
            if (eventBlockDecay.isVirtual()) {
                eventBlockDecay.setValue(true);
            }

            ConfigurationNode eventBlockGrow = rootNode.getNode("events", "block", "grow");
            if (eventBlockGrow.isVirtual()) {
                eventBlockGrow.setValue(true);
            }

            ConfigurationNode eventBlockPlace = rootNode.getNode("events", "block", "place");
            if (eventBlockPlace.isVirtual()) {
                eventBlockPlace.setValue(true);
            }

            ConfigurationNode eventEntityDeath = rootNode.getNode("events", "entity", "death");
            if (eventEntityDeath.isVirtual()) {
                eventEntityDeath.setValue(true);
            }

            ConfigurationNode eventPlayerJoin = rootNode.getNode("events", "player", "join");
            if (eventPlayerJoin.isVirtual()) {
                eventPlayerJoin.setValue(false);
            }

            ConfigurationNode eventPlayerQuit = rootNode.getNode("events", "player", "quit");
            if (eventPlayerQuit.isVirtual()) {
                eventPlayerQuit.setValue(false);
            }

            // Default parameters
            ConfigurationNode defaults = rootNode.getNode("defaults", "enabled");
            if (defaults.isVirtual()) {
                defaults.setValue(true);
            }

            ConfigurationNode defaultRadius = rootNode.getNode("defaults", "radius");
            if (defaultRadius.isVirtual()) {
                defaultRadius.setValue(5);
            }

            // Commands
            ConfigurationNode nearRadius = rootNode.getNode("commands", "near", "defaultRadius");
            if (nearRadius.isVirtual()) {
                nearRadius.setValue(5);
            }

            // Queries
            ConfigurationNode rbQueryMaxLimit = rootNode.getNode("query", "actionable", "limit");
            if (rbQueryMaxLimit.isVirtual()) {
                rbQueryMaxLimit.setValue(10000);
            }

            ConfigurationNode lQueryMaxLimit = rootNode.getNode("query", "lookup", "limit");
            if (lQueryMaxLimit.isVirtual()) {
                lQueryMaxLimit.setValue(1000);
            }

            // Save
            try {
                configManager.save(rootNode);
            } catch(IOException e) {
                // @todo handle properly
                e.printStackTrace();
            }
        } catch (IOException e) {
            // @todo handle properly
            e.printStackTrace();
        }
    }

    /**
     * Shortcut to rootNode.getNode().
     *
     * @param path Object[] Paths to desired node
     * @return ConfigurationNode
     */
    public ConfigurationNode getNode(Object... path) {
        return rootNode.getNode(path);
    }
}
