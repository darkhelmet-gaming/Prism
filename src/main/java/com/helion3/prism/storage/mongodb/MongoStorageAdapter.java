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
package com.helion3.prism.storage.mongodb;

import com.helion3.prism.Prism;
import com.helion3.prism.api.storage.StorageAdapter;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageAdapterSettings;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;

import java.util.concurrent.TimeUnit;
import java.util.List;
import java.util.ArrayList;

import org.bson.Document;

public class MongoStorageAdapter implements StorageAdapter {

    private static MongoClient mongoClient = null;
    private static MongoDatabase database;
    private final MongoRecords records;
    private final String databaseName;

    protected static String collectionEventRecordsName;

    /**
     *
     */
    public MongoStorageAdapter() {
        databaseName = Prism.getConfig().getNode("db", "name").getString();

        // Collections
        collectionEventRecordsName = "records";

        records = new MongoRecords();
    }

    /**
     * Establish connections to the database
     *
     * @return Whether we could connect properly
     */
    @Override
    public boolean connect() throws Exception {
        ServerAddress address = new ServerAddress(
            Prism.getConfig().getNode("db", "mongo", "host").getString(),
            Prism.getConfig().getNode("db", "mongo", "port").getInt()
        );
        List<MongoCredential> credentials = new ArrayList<>();

        String user = Prism.getConfig().getNode("db", "mongo", "user").getString();

        if (user != null && !user.isEmpty()) {
            credentials.add(
                MongoCredential.createCredential(
                    user,
                    databaseName,
                    Prism.getConfig().getNode("db", "mongo", "pass").getString().toCharArray()
                )
            );
        }
        mongoClient = new MongoClient(address, credentials);

        // @todo support auth: boolean auth = db.authenticate(myUserName, myPassword);

        // Connect to the database
        database = mongoClient.getDatabase(databaseName);

        // Create indexes
        try {
            getCollection(collectionEventRecordsName).createIndex(
                    new Document("Location.X", 1).append("Location.Z", 1).append("Location.Y", 1).append("Created", -1));
            getCollection(collectionEventRecordsName).createIndex(new Document("Created", -1).append("EventName", 1));

            // TTL
            IndexOptions options = new IndexOptions().expireAfter(0L, TimeUnit.SECONDS);
            getCollection(collectionEventRecordsName).createIndex(new Document("Expires", 1), options);
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
    public StorageAdapterSettings settings() {
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
    @Override
    public void close() {
        mongoClient.close();
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
