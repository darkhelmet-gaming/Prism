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
package com.helion3.prism.storage.h2;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.helion3.prism.util.DataQueries;
import org.spongepowered.api.service.sql.SqlService;

import com.helion3.prism.Prism;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageAdapterSettings;

public class H2StorageAdapter implements StorageAdapter {
    private final String tablePrefix = Prism.getConfig().getNode("db", "h2", "tablePrefix").getString();
    private final SqlService sql = Prism.getGame().getServiceManager().provide(SqlService.class).get();
    private final String dbPath = Prism.getParentDirectory().getAbsolutePath() + "/" + Prism.getConfig().getNode("db", "name").getString();
    private final StorageAdapterRecords records;
    private static DataSource db;

    /**
     * Create a new instance of the H2 storage adapter.
     */
    public H2StorageAdapter() {
        records = new H2Records();
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
            db = sql.getDataSource("jdbc:h2:" + dbPath);

            // Create table if needed
            createTables();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Create table structure if none present.
     *
     * @throws SQLException
     */
    protected void createTables() throws SQLException {
        try (Connection conn = getConnection()) {
            String records = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "records ("
                    + "id int primary key auto_increment, "
                    + DataQueries.Created + " bigint, "
                    + DataQueries.EventName + " varchar(16), "
                    + DataQueries.WorldUuid + " UUID, "
                    + DataQueries.X + " int, "
                    + DataQueries.Y + " smallint, "
                    + DataQueries.Z + " int, "
                    + DataQueries.Target + " varchar(55), "
                    + DataQueries.Player + " UUID, "
                    + DataQueries.Cause + " varchar(64))";
            conn.prepareStatement(records).execute();

            String extra = "CREATE TABLE IF NOT EXISTS " + tablePrefix + "extra ("
                    + "id int primary key auto_increment, "
                    + "record_id int, "
                    + "json varchar(30000),"
                    + "CONSTRAINT " + tablePrefix + "extra_ibfk_1 "
                    + "FOREIGN KEY (record_id) "
                    + "REFERENCES " + tablePrefix + "records (id) "
                    + "ON DELETE CASCADE"
                    + ");";
            conn.prepareStatement(extra).execute();

            String locationIndex = "CREATE INDEX IF NOT EXISTS location ON " + tablePrefix + "records("
                + DataQueries.WorldUuid + ", " + DataQueries.X + ", " + DataQueries.Y + ", " + DataQueries.Z + ")";
            conn.prepareStatement(locationIndex).execute();

            String dateIndex = "CREATE INDEX IF NOT EXISTS created ON " + tablePrefix + "records("
                    + DataQueries.Created + ")";
            conn.prepareStatement(dateIndex).execute();

            String extraIndex = "CREATE INDEX IF NOT EXISTS recordId ON " + tablePrefix + "extra(record_id)";
            conn.prepareStatement(extraIndex).execute();
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
        // @todo implement
    }

    @Override
    public boolean testConnection() throws Exception {
        // @todo implement
        return true;
    }
}
