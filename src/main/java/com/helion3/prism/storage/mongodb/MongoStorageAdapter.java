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
package com.helion3.prism.storage.mongodb;

import com.helion3.prism.Prism;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageAdapterSettings;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

public class MongoStorageAdapter implements StorageAdapter {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database;
    private final MongoPlayers players;
    private final MongoRecords records;
    private final String databaseName;

    protected static String collectionEventRecordsName;
    protected static String collectionPlayersName;

    /**
     *
     */
    public MongoStorageAdapter() {

        databaseName = Prism.getConfig().getNode("db", "name").getString();

        // Collections
        String tablePrefix = Prism.getConfig().getNode("db", "tablePrefix").getString();
        collectionEventRecordsName = tablePrefix + "EventRecord";
        collectionPlayersName = tablePrefix + "Players";

        players = new MongoPlayers();
        records = new MongoRecords();

    }

    /**
     * Establish connections to the database
     *
     * @return Whether we could connect properly
     */
    @Override
    public boolean connect() throws Exception {

        String host = Prism.getConfig().getNode("db", "mongo", "host").getString();
        int port = Prism.getConfig().getNode("db", "mongo", "port").getInt();

        mongoClient = new MongoClient(host, port);

        // @todo support auth: boolean auth = db.authenticate(myUserName,
        // myPassword);

        // Connect to the database
        database = mongoClient.getDatabase(databaseName);

        // Create indexes
        try {
            getCollection(collectionEventRecordsName).createIndex(
                    new Document("x", 1).append("z", 1).append("y", 1).append("created", -1));
            getCollection(collectionEventRecordsName).createIndex(new Document("created", -1).append("action", 1));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;

    }

    @Override
    public StorageAdapterRecords records() {
        return records;
    }

    @Override
    public MongoPlayers players() {
        return players;
    }

    @Override
    public StorageAdapterSettings settings() {
        // TODO Auto-generated method stub
        return null;
    }

   /**
    *
    * @param collectionName
    * @return
    */
   protected static MongoCollection<Document> getCollection(String collectionName) {
       try {
           return database.getCollection(collectionName);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return null;
   }

    /**
     * Close connections.
     */
    // @todo implement
    @Override
    public void close() {

    }

    /**
     * Test the connection, returns true if valid and ready, false if
     * error/null.
     *
     * @return
     * @throws Exception If connection fails
     */
    // @todo implement
    @Override
    public boolean testConnection() throws Exception {
        return false;
    }
}