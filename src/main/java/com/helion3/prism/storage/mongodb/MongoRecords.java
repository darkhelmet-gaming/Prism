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

import static com.mongodb.client.model.Filters.*;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.records.EventRecord;
import com.helion3.prism.api.records.ResultRecord;
import com.helion3.prism.api.records.ResultRecordAggregate;
import com.helion3.prism.api.records.ResultRecordComplete;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageDeleteResult;
import com.helion3.prism.api.storage.StorageWriteResult;
import com.helion3.prism.records.BlockEventRecord;
import com.mongodb.DBRef;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

public class MongoRecords implements StorageAdapterRecords {

    private final BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
   /**
    *
    */
   @Override
   public StorageWriteResult write(List<EventRecord> events) throws Exception {

       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Build an array of documents
       List<WriteModel<Document>> documents = new ArrayList<WriteModel<Document>>();
       for (EventRecord event : events) {

           Document document = new Document();

           document.put("eventName", event.getEventName());
           document.put("created", event.getDate());
           document.put("subjectName", event.getSubjectDisplayName());

           // Location
           if (event.getLocation().isPresent()) {

               // Coordinates
               Location location = event.getLocation().get();
               document.put("x", location.getPosition().getX());
               document.put("y", location.getPosition().getY());
               document.put("z", location.getPosition().getZ());

               // World
               Extent extent = location.getExtent();
               if (extent instanceof World) {
                   document.put("world", ((World) extent).getUniqueId().toString());
               }
           }

           // Block data
           if ( event instanceof BlockEventRecord ){

               BlockEventRecord blockRecord = (BlockEventRecord) event;

               if (blockRecord.getExistingBlockId().isPresent()) {
                   document.put("existingBlockId", blockRecord.getExistingBlockId().get());
               }
               if (blockRecord.getReplacementBlockId().isPresent()) {
                   document.put("replacementBlockId", blockRecord.getReplacementBlockId().get());
               }
           }

           // Source
           if (event.getSource().isPlayer()) {
               document.put("player", new DBRef(MongoStorageAdapter.collectionPlayersName, event.getSource().getSourceIdentifier()));
           } else {
               document.put("source", event.getSource().getSourceIdentifier());
           }

           // Insert
           documents.add(new InsertOneModel<Document>(document));

       }

       // Write
       collection.bulkWrite(documents, bulkWriteOptions);

       // @todo implement real results, BulkWriteResult

       return new StorageWriteResult();

   }

   /**
    * Execute a query session, for a list of resulting actions
    *
    * @param session
    * @return List of {@link com.helion3.prism.api.actions.ActionHandler}
    */
   @Override
   public List<ResultRecord> query(QuerySession session) throws Exception {

       // Prepare results
       List<ResultRecord> results = new ArrayList<ResultRecord>();

       // Get collection
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Query conditions
       // @todo needs implementation
       Document query = new Document();
       Document matcher = new Document("$match", query);

       // Session configs
       int sortDir = 1; // @todo needs implementation
       int rowLimit = 5; // @todo needs implementation
       boolean shouldGroup = false; // @todo needs implementation

       // Sorting
       Document sortFields = new Document();
       sortFields.put("created",sortDir);
       sortFields.put( "x", 1 );
       sortFields.put( "z", 1 );
       sortFields.put( "y", 1 );
       Document sorter = new Document("$sort", sortFields);

       // Offset/Limit
       Document limit = new Document("$limit", rowLimit);

       // Build aggregators
       AggregateIterable<Document> aggregated = null;
       if (shouldGroup) {

           // Grouping fields
           Document groupFields = new Document();
           groupFields.put("eventName", "$eventName");
           groupFields.put("player", "$player");
           groupFields.put("subjectName", "$subjectName");
           groupFields.put("dayOfMonth", new Document("$dayOfMonth", "$created"));
           groupFields.put("month", new Document("$month", "$created"));
           groupFields.put("year", new Document("$year", "$created"));

           Document groupHolder = new Document("_id", groupFields);
           groupHolder.put("count", new Document("$sum", 1));

           Document group = new Document("$group", groupHolder);

           // Aggregation pipeline
           List<Document> pipeline = new ArrayList<Document>();
           pipeline.add(matcher);
           pipeline.add(group);
           pipeline.add(sorter);
           pipeline.add(limit);

           aggregated = collection.aggregate(pipeline);

       } else {

           // Aggregation pipeline
           List<Document> pipeline = new ArrayList<Document>();
           pipeline.add(matcher);
           pipeline.add(sorter);
           pipeline.add(limit);

           aggregated = collection.aggregate(pipeline);

       }

       // Iterate results and build our event record list
       MongoCursor<Document> cursor = aggregated.iterator();
       try {

           MongoCollection<Document> players = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionPlayersName);

           while (cursor.hasNext()) {

               // Mongo document
               Document wrapper = cursor.next();
               Document document = shouldGroup ? (Document) wrapper.get("_id") : wrapper;

               // Build our result objectpr
               ResultRecord result = null;
               if (shouldGroup) {
                   result = new ResultRecordAggregate();
                   ((ResultRecordAggregate)result).count = (Integer) wrapper.get("count");
               } else {
                   result = new ResultRecordComplete();
               }

               // Determine the final name of the event source
               String source = "unknown";
               if (document.containsKey("player")) {
                   DBRef ref = (DBRef) document.get("player");
                   // @todo Isn't there an easier way to pull refs in v3?
                   Document player = players.find(eq("_id", ref.getId())).first();
                   source = player.getString("name");
               } else {
                   source = document.getString("source");
               }

               // Common fields
               result.eventName = document.getString("eventName");
               result.source = source;
               result.subjectName = document.getString("subjectName");

               results.add(result);

           }
       } finally {
           cursor.close();
       }

       return results;

   }

   /**
    * Given a list of parameters, will remove all matching records.
    *
    * @param query Query conditions indicating what we're purging
    * @return
    */
   // @todo implement
   @Override
   public StorageDeleteResult delete(Query query) {
       return new StorageDeleteResult();
   }
}
