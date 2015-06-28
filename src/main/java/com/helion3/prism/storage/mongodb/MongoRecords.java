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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.UUID;

import org.bson.Document;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.results.ResultRecord;
import com.helion3.prism.api.results.ResultRecordAggregate;
import com.helion3.prism.api.results.ResultRecordComplete;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageDeleteResult;
import com.helion3.prism.api.storage.StorageWriteResult;
import com.helion3.prism.records.EventRecord;
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
     * Converts a DataContainer to a Document, recursively if needed.
     * @param container Data container.
     * @return Document for Mongo storage.
     */
    private Document documentFromContainer(DataContainer container) {
        Document document = new Document();

        Set<DataQuery> keys = container.getKeys(false);
        for (DataQuery query : keys) {
            Optional<Object> optional = container.get(query);
            if (optional.isPresent()) {
                String key = query.asString(".");

                if (optional.get() instanceof ImmutableList) {
                    @SuppressWarnings("unchecked")
                    ImmutableList<DataContainer> list = (ImmutableList<DataContainer>) optional.get();
                    Iterator<DataContainer> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        DataContainer subContainer = iterator.next();
                        document.append(key, documentFromContainer(subContainer));
                    }
                } else {
                    document.append(key, optional.get());
                }
            }
        }

        return document;
   }

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

               Location location = event.getLocation().get();

               documentFromContainer(location.getBlockSnapshot().getState().toContainer());
               document.append("data", documentFromContainer(location.getBlockSnapshot().getState().toContainer()));

               System.out.println("document: " + document);


               // Coordinates
               document.put("x", location.getPosition().getX());
               document.put("y", location.getPosition().getY());
               document.put("z", location.getPosition().getZ());

               // World
               Extent extent = location.getExtent();
               if (extent instanceof World) {
                   document.put("world", ((World) extent).getUniqueId().toString());
               }
           }

           // Source
           if (event.getSource().isPlayer()) {
               document.put("player", new DBRef(MongoStorageAdapter.collectionPlayersName, event.getSource().getSourceIdentifier()));
           } else {
               document.put("source", event.getSource().getSourceIdentifier());
           }

//           Document data = new Document();

           // Store data
//           if (event.getData().isPresent()) {
//               for (Entry<String,String> entry : event.getData().get().entrySet()) {
//                   data.put(entry.getKey(), entry.getValue());
//               }
//               document.put("data", data);
//           }

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

       Query query = session.getQuery();

       // Prepare results
       List<ResultRecord> results = new ArrayList<ResultRecord>();

       // Get collection
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Query conditions
       Document conditions = new Document();

       // Actions
       if (query.getEventNames().size() > 0) {
           String matchRule = query.getEventNameMatchRule().equals(MatchRule.INCLUDE) ? "$in" : "$nin";
           conditions.append("eventName", new Document(matchRule, query.getEventNames()));
       }

       // Append all conditions
       Document matcher = new Document("$match", conditions);

       // Session configs
       int sortDir = 1; // @todo needs implementation
       int rowLimit = 5; // @todo needs implementation
       boolean shouldGroup = query.isAggregate();

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

               // Build our result object
               ResultRecord result = null;
               if (shouldGroup) {
                   result = new ResultRecordAggregate();
                   ((ResultRecordAggregate)result).count = (Integer) wrapper.get("count");
               } else {
                   // Pull record class for this event, if any
                   Class<? extends ResultRecord> clazz = Prism.getResultRecord(wrapper.getString("eventName"));
                   if (clazz != null){
                       result = clazz.newInstance();
                   } else {
                       result = new ResultRecordComplete();
                   }

                   ResultRecordComplete complete = (ResultRecordComplete) result;

                   // Location
                   if (wrapper.containsKey("world")) {
                       complete.world = Optional.fromNullable(UUID.fromString(wrapper.getString("world")));
                   } else {
                       complete.world = Optional.absent();
                   }
                   complete.x = Optional.fromNullable(wrapper.getDouble("x"));
                   complete.y = Optional.fromNullable(wrapper.getDouble("y"));
                   complete.z = Optional.fromNullable(wrapper.getDouble("z"));

                   // Data
                   Map<String,String> dataMap = null;
                   if (wrapper.containsKey("data")) {
                       dataMap = new HashMap<String,String>();
                       Document data = (Document) wrapper.get("data");
                       for (Entry<String,Object> entry : data.entrySet()) {
                           dataMap.put(entry.getKey(), (String)entry.getValue());
                       }
                   }
                   complete.data = Optional.fromNullable(dataMap);
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
