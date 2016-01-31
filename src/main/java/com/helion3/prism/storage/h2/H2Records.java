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
package com.helion3.prism.storage.h2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.helion3.prism.api.records.Result;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.profile.GameProfile;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.SQLQuery;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageDeleteResult;
import com.helion3.prism.api.storage.StorageWriteResult;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DataUtil;

public class H2Records implements StorageAdapterRecords {
    private final String tablePrefix = Prism.getConfig().getNode("db", "mysql", "tablePrefix").getString();

    @Override
    public StorageWriteResult write(List<DataContainer> containers) throws Exception {
        Connection conn = H2StorageAdapter.getConnection();
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO " + tablePrefix + "records(created, eventName, world, x, y, z, target, player, cause) values(?, ?, ?, ?, ?, ?, ?, ?, ?)";
            statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            for (DataContainer container : containers) {
                DataView location = container.getView(DataQueries.Location).get();

                String playerUUID = null;
                Optional<String> player = container.getString(DataQueries.Player);
                if (player.isPresent()) {
                    playerUUID = player.get();
                }

                statement.setLong( 1, System.currentTimeMillis() / 1000L );
                statement.setObject(2, container.getString(DataQueries.EventName).get());
                statement.setObject(3, location.getString(DataQueries.WorldUuid).get());
                statement.setInt(4, location.getInt(DataQueries.X).get());
                statement.setInt(5, location.getInt(DataQueries.Y).get());
                statement.setInt(6, location.getInt(DataQueries.Z).get());
                statement.setString(7, container.getString(DataQueries.Target).get());
                statement.setString(8, playerUUID);
                statement.setString(9, container.getString(DataQueries.Cause).orElse(null));

                // Remove some data not needed for extra storage
                container.remove(DataQueries.Location);
                container.remove(DataQueries.EventName);
                container.remove(DataQueries.Player);
                container.remove(DataQueries.Cause);
                container.remove(DataQueries.Target);

                statement.executeUpdate();
                ResultSet keys = statement.getGeneratedKeys();

                while (keys.next()) {
                    writeExtraData(keys.getInt(1), DataUtil.jsonFromDataView(container).toString());
                }
            }
        }
        finally {
            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return null;
    }

    /**
     * Writes extra JSON to a separate table because we don't always need it.
     *
     * @param recordId Primary key of the parent record.
     * @param json
     * @return
     * @throws Exception
     */
    protected StorageWriteResult writeExtraData(int recordId, String json) throws Exception {
        Connection conn = H2StorageAdapter.getConnection();
        PreparedStatement statement = null;

        try {
            String sql = "INSERT INTO " + tablePrefix + "extra(record_id, json) values(?, ?)";
            statement = conn.prepareStatement(sql);
            statement.setInt(1, recordId);
            statement.setString(2, json);
            statement.executeUpdate();
        }
        finally {
            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return null;
    }

    @Override
    public CompletableFuture<List<Result>> query(QuerySession session, boolean translate) throws Exception {
        // Prepare results
        List<Result> results = new ArrayList<Result>();
        CompletableFuture<List<Result>> future = new CompletableFuture<List<Result>>();

        Connection conn = H2StorageAdapter.getConnection();
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            List<UUID> uuidsPendingLookup = new ArrayList<UUID>();

            SQLQuery query = SQLQuery.from(session);
            Prism.getLogger().debug("H2 SQL Query: " + query);

            // Build query
            statement = conn.prepareStatement(query.toString());
            rs = statement.executeQuery();

            while (rs.next()) {
                Result result = Result.from(rs.getString("eventName"), session.getQuery().isAggregate());

                // Restore the data container
                DataContainer data = new MemoryDataContainer();
                data.set(DataQueries.EventName, rs.getString("eventName"));
                data.set(DataQueries.Target, rs.getString("target"));

                if (session.getQuery().isAggregate()) {
                    data.set(DataQueries.Count, rs.getInt("total"));
                } else {
                    DataContainer loc = new MemoryDataContainer();
                    loc.set(DataQueries.X, rs.getInt("x"));
                    loc.set(DataQueries.Y, rs.getInt("y"));
                    loc.set(DataQueries.Z, rs.getInt("z"));
                    loc.set(DataQueries.WorldUuid, rs.getString("world"));
                    data.set(DataQueries.Location, loc);

                    JsonObject json = new JsonParser().parse(rs.getString("json")).getAsJsonObject();
                    DataView extra = DataUtil.dataViewFromJson(json);

                    for (DataQuery key : extra.getKeys(false)) {
                        data.set(key, extra.get(key).get());
                    }
                }

                // Determine the final name of the event source
                if (rs.getString("player") != null && !rs.getString("player").isEmpty()) {
                    data.set(DataQueries.Cause, rs.getString("player"));

                    if (translate) {
                        uuidsPendingLookup.add(UUID.fromString(rs.getString("player")));
                    }
                } else {
                    data.set(DataQueries.Cause, rs.getString("cause"));
                }

                result.data = data;
                results.add(result);
            }

            if (translate && !uuidsPendingLookup.isEmpty()) {
                DataUtil.translateUuidsToNames(results, uuidsPendingLookup).thenAccept(finalResults -> {
                    future.complete(finalResults);
                });
            } else {
                future.complete(results);
            }
        }
        finally {
            if (rs != null) {
                rs.close();
            }

            if (statement != null) {
                statement.close();
            }

            conn.close();
        }

        return future;
    }

    @Override
    public StorageDeleteResult delete(Query query) throws Exception {
        // @todo implement
        return null;
    }
}
