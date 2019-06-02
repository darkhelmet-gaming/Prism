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
package com.helion3.prism.storage.mysql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import com.helion3.prism.Prism;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageAdapterSettings;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DateUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.spongepowered.api.scheduler.Task;

public class MySQLStorageAdapter implements StorageAdapter {

    private final String expiration = Prism.getInstance().getConfig().getStorageCategory().getExpireRecords();
    private final String tablePrefix = Prism.getInstance().getConfig().getStorageCategory().getTablePrefix();
    private final int purgeBatchLimit = Prism.getInstance().getConfig().getStorageCategory().getPurgeBatchLimit();
    private final StorageAdapterRecords records;
    private static HikariDataSource db;
    private final String dns;

    /**
     * Create a new instance of the H2 storage adapter.
     */
    public MySQLStorageAdapter() {
        records = new MySQLRecords();

        dns = String.format("jdbc:mysql://%s/%s",
                Prism.getInstance().getConfig().getStorageCategory().getAddress(),
                Prism.getInstance().getConfig().getStorageCategory().getDatabase()
        );
    }

    /**
     * Get the connection.
     *
     * @return Connection
     * @throws SQLException
     */
    protected static Connection getConnection() throws SQLException {
        return db.getConnection();
    }

    @Override
    public boolean connect() throws Exception {
        try {
            // Get data source
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(dns);
            config.setUsername(Prism.getInstance().getConfig().getStorageCategory().getUsername());
            config.setPassword(Prism.getInstance().getConfig().getStorageCategory().getPassword());
            config.setMaximumPoolSize(Prism.getInstance().getConfig().getStorageCategory().getMaximumPoolSize());
            config.setMinimumIdle(Prism.getInstance().getConfig().getStorageCategory().getMinimumIdle());

            db = new HikariDataSource(config);

            // Create table if needed
            createTables();

            // Purge async
            Task.builder()
                    .async()
                    .name("PrismMySQLPurge")
                    .execute(this::purge)
                    .submit(Prism.getInstance().getPluginContainer());

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create table structure if none present.
     *
     * @throws SQLException
     */
    protected void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            String records = "CREATE TABLE IF NOT EXISTS "
                    + tablePrefix + "records ("
                    + "id int(10) unsigned NOT NULL AUTO_INCREMENT, "
                    + DataQueries.Created + " int(10) unsigned NOT NULL, "
                    + DataQueries.EventName + " varchar(16) NOT NULL, "
                    + DataQueries.WorldUuid + " binary(16) NOT NULL, "
                    + DataQueries.X + " int(10) NOT NULL, "
                    + DataQueries.Y + " smallint(5) NOT NULL, "
                    + DataQueries.Z + " int(10) NOT NULL, "
                    + DataQueries.Target + " varchar(255), "
                    + DataQueries.Player + " binary(16), "
                    + DataQueries.Cause + " varchar(55), "
                    + "PRIMARY KEY (`id`), "
                    + "KEY  `location` (`"+ DataQueries.WorldUuid
                        + "`, `" + DataQueries.X
                        + "`, `" + DataQueries.Z
                        + "`, `" + DataQueries.Y
                    + "`), "
                    + "KEY `created` (`created`)"
                    + ") ENGINE=InnoDB DEFAULT CHARACTER SET utf8 " +
                    "  DEFAULT COLLATE utf8_general_ci;";
            conn.prepareStatement(records).execute();

            String extra = "CREATE TABLE IF NOT EXISTS "
                    + tablePrefix + "extra ("
                    + "id int(10) unsigned NOT NULL AUTO_INCREMENT, "
                    + "record_id int(10) unsigned NOT NULL, "
                    + "json TEXT, "
                    + "PRIMARY KEY (`id`), "
                    + "KEY `record_id` (`record_id`), "
                    + "CONSTRAINT " + tablePrefix + "extra_ibfk_1 "
                    + "FOREIGN KEY (record_id) "
                    + "REFERENCES " + tablePrefix + "records (id) "
                    + "ON DELETE CASCADE"
                    + ") ENGINE=InnoDB DEFAULT CHARACTER SET utf8 "
                    + "DEFAULT COLLATE utf8_general_ci;";
            conn.prepareStatement(extra).execute();

            if (Prism.getInstance().getConfig().getGeneralCategory().getSchemaVersion() == 1) {
                // Expand target: 55 -> 255
                conn.prepareStatement(String.format("ALTER TABLE %srecords MODIFY %s varchar(255);",
                        tablePrefix,
                        DataQueries.Target
                )).execute();

                Prism.getInstance().getConfig().getGeneralCategory().setSchemaVersion(2);
                Prism.getInstance().getConfiguration().saveConfiguration();
            }
        }
    }

    /**
     * Removes expires records and extra information from the database.
     */
    protected void purge() {
        try {
            Prism.getInstance().getLogger().info("Purging MySQL database...");
            long purged = 0;
            while (true) {
                int count = purgeRecords();
                if (count == 0) {
                    break;
                }

                purged += count;
                Prism.getInstance().getLogger().info("Deleted {} records", purged);
            }

            Prism.getInstance().getLogger().info("Finished purging MySQL database");
        } catch (Exception ex) {
            Prism.getInstance().getLogger().error("Encountered an error while purging MySQL database", ex);
        }
    }

    /**
     * Removes expires records from the database.
     *
     * @return The amount of rows removed.
     * @throws Exception
     */
    protected int purgeRecords() throws Exception {
        Date date = DateUtil.parseTimeStringToDate(expiration, false);
        if (date == null) {
            throw new IllegalArgumentException("Failed to parse expiration");
        }

        if (purgeBatchLimit <= 0) {
            throw new IllegalArgumentException("PurgeBatchLimit cannot be equal to or lower than 0");
        }

        String sql = "DELETE FROM " + tablePrefix + "records "
                + "WHERE " + tablePrefix + "records.created <= ? "
                + "LIMIT ?;";
        try (Connection conn = getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setLong(1, date.getTime() / 1000);
            statement.setInt(2, purgeBatchLimit);
            return statement.executeUpdate();
        }
    }

    @Override
    public StorageAdapterRecords records() {
        return records;
    }

    @Override
    public StorageAdapterSettings settings() {
        // @todo implement
        return null;
    }

    @Override
    public void close() {
        db.close();
    }

    @Override
    public boolean testConnection() throws Exception {
        // @todo implement
        return true;
    }
}
