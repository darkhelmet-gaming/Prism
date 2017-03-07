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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.*;
import com.helion3.prism.api.records.Result;
import org.bson.Document;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

import com.google.common.collect.Range;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.ConditionGroup.Operator;
import com.helion3.prism.api.storage.StorageAdapterRecords;
import com.helion3.prism.api.storage.StorageDeleteResult;
import com.helion3.prism.api.storage.StorageWriteResult;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DataUtil;
import com.helion3.prism.util.DateUtil;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.BulkWriteOptions;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.WriteModel;

public class MongoRecords implements StorageAdapterRecords {
    private final BulkWriteOptions bulkWriteOptions = new BulkWriteOptions().ordered(false);
    private final String expiration = Prism.getConfig().getNode("storage", "expireRecords").getString();

    /**
     * Converts a DataView to a Document, recursively if needed.
     *
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
                    List<Object> convertedList = new ArrayList<>();
                    for (Object object : (List<?>) optional.get()) {

                        if (object instanceof DataView) {
                            convertedList.add(documentFromView((DataView) object));
                        }
                        else if (object.getClass().isEnum()) {
                            // Ignoring, this data should exist elsewhere in the document.
                            // this is ConnectedDirections and other vanilla manipulators
                            //convertedList.add(object.toString());
                        }
                        else if (DataUtil.isPrimitiveType(object)) {
                            convertedList.add(optional.get());
                            break;
                        }
                        else {
                            Prism.getLogger().error("Unsupported list data type: " + object.getClass().getName());
                        }
                    }

                    if (!convertedList.isEmpty()) {
                        document.append(key, convertedList);
                    }
                }
                else if (optional.get() instanceof DataView) {
                    DataView subView = (DataView) optional.get();
                    document.append(key, documentFromView(subView));
                }
                else {
                    if (key.equals(DataQueries.Player.toString())) {
                        document.append(DataQueries.Player.toString(), optional.get());
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
            DataQuery keyQuery = DataQuery.of(key);
            Object object = document.get(key);

            if (object instanceof Document) {
                result.set(keyQuery, documentToDataContainer((Document) object));
            } else {
                result.set(keyQuery, object);
            }
        }

        return result;
   }

   @Override
   public StorageWriteResult write(List<DataContainer> containers) throws Exception {
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Build an array of documents
       List<WriteModel<Document>> documents = new ArrayList<>();
       for (DataContainer container : containers) {
           Document document = documentFromView(container);

           //Prism.getLogger().debug(DataUtil.jsonFromDataView(container).toString());

           // TTL
           document.append("Expires", DateUtil.parseTimeStringToDate(expiration, true));

           // Insert
           documents.add(new InsertOneModel<>(document));
       }

       // Write
       collection.bulkWrite(documents, bulkWriteOptions);

       // @todo implement real results, BulkWriteResult

       return new StorageWriteResult();
   }

   /**
    * Recursive method of building condition documents.
    *
    * @param fieldsOrGroups List<Condition>
    * @return Document
    */
   private Document buildConditions(List<Condition> fieldsOrGroups) {
       Document conditions = new Document();

       for (Condition fieldOrGroup : fieldsOrGroups) {
           if (fieldOrGroup instanceof ConditionGroup) {
               ConditionGroup group = (ConditionGroup) fieldOrGroup;
               Document subDoc = buildConditions(group.getConditions());

               if (group.getOperator().equals(Operator.OR)) {
                   conditions.append("$or", subDoc);
               } else {
                   conditions.putAll(subDoc);
               }
           } else {
               FieldCondition field = (FieldCondition) fieldOrGroup;

               Document matcher;
               if (conditions.containsKey(field.getFieldName().toString())) {
                   matcher = (Document) conditions.get(field.getFieldName().toString());
               } else {
                   matcher = new Document();
               }

               // Match an array of items
               if (field.getValue() instanceof List) {
                   matcher.append(field.getMatchRule().equals(MatchRule.INCLUDES) ? "$in" : "$nin", field.getValue());
                   conditions.put(field.getFieldName().toString(), matcher);
               }

               else if (field.getMatchRule().equals(MatchRule.EQUALS)) {
                   conditions.put(field.getFieldName().toString(), field.getValue());
               }

               else if (field.getMatchRule().equals(MatchRule.GREATER_THAN_EQUAL)) {
                   matcher.append("$gte", field.getValue());
                   conditions.put(field.getFieldName().toString(), matcher);
               }

               else if (field.getMatchRule().equals(MatchRule.LESS_THAN_EQUAL)) {
                   matcher.append("$lte", field.getValue());
                   conditions.put(field.getFieldName().toString(), matcher);
               }

               else if (field.getMatchRule().equals(MatchRule.BETWEEN)) {
                   if (!(field.getValue() instanceof Range)) {
                       throw new IllegalArgumentException("\"Between\" match value must be a Range.");
                   }

                   Range<?> range = (Range<?>) field.getValue();

                   Document between = new Document("$gte", range.lowerEndpoint()).append("$lte", range.upperEndpoint());
                   conditions.put(field.getFieldName().toString(), between);
               }
           }
       }

       return conditions;
   }

   @Override
   public CompletableFuture<List<Result>> query(QuerySession session, boolean translate) throws Exception {
       Query query = session.getQuery();
       checkNotNull(query);

       // Prepare results
       List<Result> results = new ArrayList<>();
       CompletableFuture<List<Result>> future = new CompletableFuture<>();

       // Get collection
       MongoCollection<Document> collection = MongoStorageAdapter.getCollection(MongoStorageAdapter.collectionEventRecordsName);

       // Append all conditions
       Document matcher = new Document("$match", buildConditions(query.getConditions()));

       // Sorting. Newest first for rollback and oldest first for restore.
       Document sortFields = new Document();
       sortFields.put(DataQueries.Created.toString(), session.getSortBy().getValue());
       Document sorter = new Document("$sort", sortFields);

       // Offset/Limit
       Document limit = new Document("$limit", query.getLimit());

       // Build aggregators
       final AggregateIterable<Document> aggregated;
       if (!session.hasFlag(Flag.NO_GROUP)) {
           // Grouping fields
           Document groupFields = new Document();
           groupFields.put(DataQueries.EventName.toString(), "$" + DataQueries.EventName);
           groupFields.put(DataQueries.Player.toString(), "$" + DataQueries.Player);
           groupFields.put(DataQueries.Cause.toString(), "$" + DataQueries.Cause);
           groupFields.put(DataQueries.Target.toString(), "$" + DataQueries.Target);
           // Entity
           groupFields.put(DataQueries.Entity.toString(), "$" + DataQueries.Entity.then(DataQueries.EntityType));
           // Day
           groupFields.put("dayOfMonth", new Document("$dayOfMonth", "$" + DataQueries.Created));
           groupFields.put("month", new Document("$month", "$" + DataQueries.Created));
           groupFields.put("year", new Document("$year", "$" + DataQueries.Created));

           Document groupHolder = new Document("_id", groupFields);
           groupHolder.put(DataQueries.Count.toString(), new Document("$sum", 1));

           Document group = new Document("$group", groupHolder);

           // Aggregation pipeline
           List<Document> pipeline = new ArrayList<>();
           pipeline.add(matcher);
           pipeline.add(group);
           pipeline.add(sorter);
           pipeline.add(limit);

           aggregated = collection.aggregate(pipeline);
           Prism.getLogger().debug("MongoDB Query: " + pipeline);
       } else {
           // Aggregation pipeline
           List<Document> pipeline = new ArrayList<>();
           pipeline.add(matcher);
           pipeline.add(sorter);
           pipeline.add(limit);

           aggregated = collection.aggregate(pipeline);
           Prism.getLogger().debug("MongoDB Query: " + pipeline);
       }

       // Iterate results and build our event record list
       try (MongoCursor<Document> cursor = aggregated.iterator()) {
           List<UUID> uuidsPendingLookup = new ArrayList<>();

           while (cursor.hasNext()) {
               // Mongo document
               Document wrapper = cursor.next();
               Document document = session.hasFlag(Flag.NO_GROUP) ? wrapper : (Document) wrapper.get("_id");

               DataContainer data = documentToDataContainer(document);

               if (!session.hasFlag(Flag.NO_GROUP)) {
                   data.set(DataQueries.Count, wrapper.get(DataQueries.Count.toString()));
               }

               // Build our result object
               Result result = Result.from(wrapper.getString(DataQueries.EventName.toString()), !session.hasFlag(Flag.NO_GROUP));

               // Determine the final name of the event source
               if (document.containsKey(DataQueries.Player.toString())) {
                   String uuid = document.getString(DataQueries.Player.toString());
                   data.set(DataQueries.Cause, uuid);

                   if (translate) {
                       uuidsPendingLookup.add(UUID.fromString(uuid));
                   }
               } else {
                   data.set(DataQueries.Cause, document.getString(DataQueries.Cause.toString()));
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
