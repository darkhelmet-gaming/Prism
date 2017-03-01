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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.records.Result;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

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
    private final String tablePrefix = Prism.getConfig().getNode("db", "h2", "tablePrefix").getString();

    @Override
    public StorageWriteResult write(List<DataContainer> containers) throws Exception {
        String sql = String.format("INSERT INTO %srecords(%s, %s, %s, %s, %s, %s, %s, %s, %s)" +
                        " values(?, ?, ?, ?, ?, ?, ?, ?, ?)",
                tablePrefix,
                DataQueries.Created, DataQueries.EventName, DataQueries.WorldUuid,
                DataQueries.X, DataQueries.Y, DataQueries.Z,
                DataQueries.Target, DataQueries.Player, DataQueries.Cause
        );

        try (Connection conn = H2StorageAdapter.getConnection(); PreparedStatement statement = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

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
        String sql = "INSERT INTO " + tablePrefix + "extra(record_id, json) values(?, ?)";

        try (Connection conn = H2StorageAdapter.getConnection(); PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, recordId);
            statement.setString(2, json);
            statement.executeUpdate();
        }

        return null;
    }

    @Override
    public CompletableFuture<List<Result>> query(QuerySession session, boolean translate) throws Exception {
        // Prepare results
        List<Result> results = new ArrayList<>();
        CompletableFuture<List<Result>> future = new CompletableFuture<>();

        SQLQuery query = H2SQLQuery.from(session);
        Prism.getLogger().debug("H2 SQL Query: " + query);

        try (Connection conn = H2StorageAdapter.getConnection(); PreparedStatement statement = conn.prepareStatement(query.toString()); ResultSet rs = statement.executeQuery()) {
            List<UUID> uuidsPendingLookup = new ArrayList<>();

            while (rs.next()) {
                Result result = Result.from(rs.getString(DataQueries.EventName.toString()), !session.hasFlag(Flag.NO_GROUP));

                // Restore the data container
                DataContainer data = new MemoryDataContainer();
                data.set(DataQueries.EventName, rs.getString(DataQueries.EventName.toString()));
                String target = rs.getString(DataQueries.Target.toString());
                data.set(DataQueries.Target, target != null ? target : "");

                if (!session.hasFlag(Flag.NO_GROUP)) {
                    data.set(DataQueries.Count, rs.getInt("total"));
                } else {
                    DataContainer loc = new MemoryDataContainer();
                    loc.set(DataQueries.X, rs.getInt(DataQueries.X.toString()));
                    loc.set(DataQueries.Y, rs.getInt(DataQueries.Y.toString()));
                    loc.set(DataQueries.Z, rs.getInt(DataQueries.Z.toString()));
                    loc.set(DataQueries.WorldUuid, rs.getString(DataQueries.WorldUuid.toString()));
                    data.set(DataQueries.Location, loc);

                    data.set(DataQueries.Created, rs.getLong(DataQueries.Created.toString()));

                    if (rs.getString("json") != null) {
                        JsonObject json = new JsonParser().parse(rs.getString("json")).getAsJsonObject();
                        DataView extra = DataUtil.dataViewFromJson(json);

                        for (DataQuery key : extra.getKeys(false)) {
                            data.set(key, extra.get(key).get());
                        }
                    }
                }

                // Determine the final name of the event source
                String player = rs.getString(DataQueries.Player.toString());
                if (player != null && !player.isEmpty()) {
                    data.set(DataQueries.Cause, player);

                    if (translate) {
                        uuidsPendingLookup.add(UUID.fromString(player));
                    }
                } else {
                    data.set(DataQueries.Cause, rs.getString(DataQueries.Cause.toString()));
                }

                result.data = data;
                results.add(result);
            }

            if (translate && !uuidsPendingLookup.isEmpty()) {
                DataUtil.translateUuidsToNames(results, uuidsPendingLookup).thenAccept(future::complete);
            } else {
                future.complete(results);
            }
        }

        return future;
    }

    @Override
    public StorageDeleteResult delete(Query query) throws Exception {
        // @todo implement
        return null;
    }
}
