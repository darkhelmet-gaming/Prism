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
package com.helion3.prism.util;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.Map.Entry;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.helion3.prism.api.records.Result;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helion3.prism.Prism;
import org.spongepowered.api.profile.GameProfile;

public class DataUtil {
    private DataUtil() {}

    /**
     * Checks an object against known primitive object types.
     *
     * @param object
     * @return boolean If object is a primitive type
     */
    public static boolean isPrimitiveType(Object object) {
        return (object instanceof Boolean ||
                object instanceof Byte ||
                object instanceof Character ||
                object instanceof Double ||
                object instanceof Float ||
                object instanceof Integer ||
                object instanceof Long ||
                object instanceof Short ||
                object instanceof String);
    }

    /**
     * Build a DataView from provided JSON.
     * @param json JsonObject
     * @return DataView
     */
    public static DataView dataViewFromJson(JsonObject json) {
        DataContainer data = new MemoryDataContainer();
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            Optional<Object> value = jsonElementToObject(entry.getValue());
            if (value.isPresent()) {
                data.set(DataQuery.of(entry.getKey()), value.get());
            } else {
                Prism.getLogger().error(String.format("Failed to transform %s data.", entry.getKey()));
            }
        }

        return data;
    }

    /**
     * Attempts to convert a JsonElement to an a known type.
     *
     * @param element JsonElement
     * @return Optional<Object>
     */
    private static Optional<Object> jsonElementToObject(JsonElement element) {
        Object result = null;

        if (element.isJsonObject()) {
            result = dataViewFromJson(element.getAsJsonObject());
        }
        else if (element.isJsonPrimitive()) {
            JsonPrimitive prim = element.getAsJsonPrimitive();

            if (prim.isBoolean()) {
                result = prim.getAsBoolean();
            }
            else if (prim.isNumber()) {
                result = prim.getAsNumber().intValue();
            }
            else if (prim.isString()) {
                result = prim.getAsString();
            }
        }
        else if (element.isJsonArray()) {
            List<Object> list = new ArrayList<Object>();
            JsonArray arr = element.getAsJsonArray();
            arr.forEach(new Consumer<JsonElement>() {
                @Override
                public void accept(JsonElement t) {
                    Optional<Object> translated = jsonElementToObject(t);
                    if (translated.isPresent()) {
                        list.add(translated.get());
                    }
                }
            });

            result = list;
        }

        return Optional.ofNullable(result);
    }

    /**
     * Converts a DataView object into a JsonObject.
     *
     * @param view DataView
     * @return JsonObject JsonObject representation of the DataView
     */
    public static JsonObject jsonFromDataView(DataView view) {
        JsonObject json = new JsonObject();
        Gson gson = new GsonBuilder().create();

        Set<DataQuery> keys = view.getKeys(false);
        for (DataQuery query : keys) {
            Optional<Object> optional = view.get(query);
            if (optional.isPresent()) {
                String key = query.asString(".");
                List<Object> convertedList = new ArrayList<Object>();

                if (optional.get() instanceof List) {
                    List<?> list = (List<?>) optional.get();
                    Iterator<?> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        Object object = iterator.next();

                        if (object instanceof DataView) {
                            convertedList.add(jsonFromDataView((DataView) object));
                        }
                        else if (object instanceof List) {
                            convertedList.add(gson.toJsonTree(object).getAsJsonArray());
                        }
                        else if (object.getClass().isEnum()) {
                            //convertedList.add(object.toString());
                        }
                        else if (DataUtil.isPrimitiveType(object)) {
                            JsonArray array = gson.toJsonTree(optional.get()).getAsJsonArray();
                            convertedList.add(array);
                            break;
                        }
                        else {
                            Prism.getLogger().error("Unsupported json list data type: " + object.getClass().getName() + " for key " + key);
                        }
                    }

                    if (!convertedList.isEmpty()) {
                        JsonArray array = gson.toJsonTree(convertedList).getAsJsonArray();
                        json.add(key, array);
                    }
                }
                else if (optional.get() instanceof DataView) {
                    json.add(key, jsonFromDataView((DataView) optional.get()));
                }
                else {
                    Object obj = optional.get();

                    if (obj instanceof String) {
                        json.addProperty(key, (String) obj);
                    }
                    else if (obj instanceof Number) {
                        json.addProperty(key, (Number) obj);
                    }
                    else if (obj instanceof Boolean) {
                        json.addProperty(key, (Boolean) obj);
                    }
                    else if (obj instanceof Character) {
                        json.addProperty(key, (Character) obj);
                    }
                }
            }
        }

        return json;
    }


    public static CompletableFuture<List<Result>> translateUuidsToNames(List<Result> results, List<UUID> uuidsPendingLookup) {
        CompletableFuture<List<Result>> future = new CompletableFuture<List<Result>>();

        ListenableFuture<Collection<GameProfile>> profiles = Prism.getGame().getServer().getGameProfileManager().getAllById(uuidsPendingLookup, true);
        profiles.addListener(() -> {
            try {
                for (GameProfile profile : profiles.get()) {
                    for (Result r : results) {
                        Optional<Object> cause = r.data.get(DataQueries.Cause);
                        if (cause.isPresent() && ((String) cause.get()).equals(profile.getUniqueId().toString())) {
                            r.data.set(DataQueries.Cause, profile.getName());
                        }
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            future.complete(results);
        }, MoreExecutors.sameThreadExecutor());

        return future;
    }
}
