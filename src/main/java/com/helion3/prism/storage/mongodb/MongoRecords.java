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
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bson.Document;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.results.ResultRecord;
import com.helion3.prism.api.results.ResultRecordAggregate;
import com.helion3.prism.api.results.ResultRecordComplete;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageDeleteResult;
import com.helion3.prism.api.storage.StorageWriteResult;
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
     * Converts a DataView to a Document, recursively if needed.
     * @param view Data view/container.
     * @return Document for Mongo storage.
     */
    private Document documentFromView(DataView view) {
        Document document = new Document();

        Set<DataQuery> keys = view.getKeys(false);
        for (DataQuery query : keys) {
            Optional<Object> optional = view.get(query);
            if (optional.isPresent()) {
                String key = query.asString(".");

                if (optional.get() instanceof List) {
                    List<?> list = (List<?>) optional.get();
                    Iterator<?> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Object object = iterator.next();

                        if (object instanceof DataView) {
                            DataView subView = (DataView) object;
                            document.append(key, documentFromView(subView));
                        } else {
                            Prism.getLogger().error("Unsupported list data type: " + object.getClass().getName());
                        }
                    }
                }
                else if (optional.get() instanceof DataView) {
                    DataView subView = (DataView) optional.get();
                    document.append(key, documentFromView(subView));
                }
                else {
                    if (key.equals("player")) {
                        document.append("player", new DBRef(MongoStorageAdapter.collectionPlayersName, optional.get()));
                    } else {
                        document.append(key, optional.get());
                    }
                }
            }
        }

        return document;
    }

    /**
     * Convert a mongo Document to a DataContainer.
     * @param document Mongo document.
     * @return Data container.
     */
    private DataContainer documentToDataContainer(Document document) {
        DataContainer result = new MemoryDataContainer();

        for (String key : document.keySet()) {
            DataQuery keyQuery = new DataQuery(key);
            Object object = document.get(key);

            if (object instanceof Document) {
                result.set(keyQuery, documentToDataContainer((Document) object));
            } else {
                result.set(keyQuery, object);
            }
        }

        return result;
    }

   /**
    *
    */
   @Override
   public StorageWriteResult write(List<DataContainer> containers) throws Exception {
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Build an array of documents
       List<WriteModel<Document>> documents = new ArrayList<WriteModel<Document>>();
       for (DataContainer container : containers) {
           Document document = documentFromView(container);

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
       if (!session.getQuery().isPresent()) {
           throw new IllegalArgumentException("No valid query in session.");
       }

       Query query = session.getQuery().get();

       // Prepare results
       List<ResultRecord> results = new ArrayList<ResultRecord>();

       // Get collection
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Query conditions
       Document conditions = new Document();
       for (Condition condition : query.getConditions()) {
           Object value = condition.getValue();

           // Match an array of items
           if (value instanceof List) {
               String matchRule = condition.getMatchRule().equals(MatchRule.INCLUDES) ? "$in" : "$nin";
               conditions.append(condition.getDataQuery().toString(), new Document(matchRule, value));
           }
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

               DataContainer data = documentToDataContainer(document);

               if (shouldGroup) {
                   data.set(new DataQuery("count"), wrapper.get("count"));
               }

               // Build our result object
               ResultRecord result = null;
               if (shouldGroup) {
                   result = new ResultRecordAggregate();
               } else {
                   // Pull record class for this event, if any
                   Class<? extends ResultRecord> clazz = Prism.getResultRecord(wrapper.getString("eventName"));
                   if (clazz != null){
                       result = clazz.newInstance();
                   } else {
                       result = new ResultRecordComplete();
                   }
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

               data.set(DataQuery.of("source"), source);

               result.data = data;
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
