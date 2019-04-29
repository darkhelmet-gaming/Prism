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
public class StorageCategory {

    @Setting(value = "address", comment = ""
            + "The address and port for the database\n"
            + " - Specify as 'host:port' if it differs from default")
    private String address = "localhost";

    @Setting(value = "database", comment = "The name of the database")
    private String database = "prism";

    @Setting(value = "engine", comment = ""
            + "The database engine\n"
            + "   (H2, MongoDB, MySQL)")
    private String engine = "h2";

    @Setting(value = "expire-records", comment = "The amount of time records are stored for")
    private String expireRecords = "4w";

    @Setting(value = "maximum-pool-size", comment = "Maximum size of the MySQL connection pool")
    private int maximumPoolSize = 10;

    @Setting(value = "minimum-idle", comment = "Minimum number of idle connections that the pool will try to maintain")
    private int minimumIdle = 2;

    @Setting(value = "password", comment = "Credential for the database")
    private String password = "";

    @Setting(value = "purge-batch-limit", comment = "Amount of records to purge at a time")
    private int purgeBatchLimit = 100000;

    @Setting(value = "table-prefix", comment = "The prefix of all SQL tables.")
    private String tablePrefix = "prism_";

    @Setting(value = "username", comment = "Credential for the database")
    private String username = "prism";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getEngine() {
        return engine;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getExpireRecords() {
        return expireRecords;
    }

    public void setExpireRecords(String expireRecords) {
        this.expireRecords = expireRecords;
    }

    public int getMaximumPoolSize() {
        return maximumPoolSize;
    }

    public void setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
    }

    public int getMinimumIdle() {
        return minimumIdle;
    }

    public void setMinimumIdle(int minimumIdle) {
        this.minimumIdle = minimumIdle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPurgeBatchLimit() {
        return purgeBatchLimit;
    }

    public void setPurgeBatchLimit(int purgeBatchLimit) {
        this.purgeBatchLimit = purgeBatchLimit;
    }

    public String getTablePrefix() {
        return tablePrefix;
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}