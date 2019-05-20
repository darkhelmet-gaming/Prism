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

package com.helion3.prism.util;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.Map.Entry;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.helion3.prism.api.records.Result;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.translate.CharSequenceTranslator;
import org.apache.commons.lang3.text.translate.UnicodeEscaper;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.helion3.prism.Prism;
import org.spongepowered.api.profile.GameProfile;

public class DataUtil {

    private static final CharSequenceTranslator CHAR_SEQUENCE_TRANSLATOR = StringEscapeUtils.ESCAPE_JAVA.with(
            UnicodeEscaper.between(0, 47),
            // 0 - 9
            UnicodeEscaper.between(58, 64),
            // A - Z
            UnicodeEscaper.between(91, 96),
            // a - z
            UnicodeEscaper.between(123, Integer.MAX_VALUE)
    );

    private DataUtil() {
    }

    /**
     * Checks an object against known primitive object types.
     *
     * @param object
     * @return boolean If object is a primitive type
     */
    public static boolean isPrimitiveType(Object object) {
        return object instanceof Boolean
                || object instanceof Byte
                || object instanceof Character
                || object instanceof Double
                || object instanceof Float
                || object instanceof Integer
                || object instanceof Long
                || object instanceof Short
                || object instanceof String;
    }

    /**
     * Encodes all non-alphanumeric characters in the provided DataQuery,
     * the encodes parts are then joined with a non-encoded symbol.
     *
     * Fixes https://github.com/prism/Prism/issues/90
     *
     * @param dataQuery Unescaped DataQuery
     * @return Escaped string
     */
    public static String escapeQuery(DataQuery dataQuery) {
        List<String> parts = Lists.newArrayList();

        for (String part : dataQuery.getParts()) {
            parts.add(escape(part));
        }

        return StringUtils.join(parts, '/');
    }

    /**
     * Encodes all non-alphanumeric characters in the provided String.
     *
     * @param string Unescaped string
     * @return string Escaped string
     */
    public static String escape(String string) {
        return CHAR_SEQUENCE_TRANSLATOR.translate(string);
    }

    /**
     * Decodes all encoded characters in the provided String,
     * the string is split by a non-encoded symbol and each part is then decoded
     * and finally a DataQuery is created using the decoded parts.
     *
     * Fixes https://github.com/prism/Prism/issues/90
     *
     * @param string Escaped string
     * @return Unescaped DataQuery
     */
    public static DataQuery unescapeQuery(String string) {
        List<String> parts = Lists.newArrayList();
        for (String part : StringUtils.split(string, '/')) {
            parts.add(unescape(part));
        }

        return DataQuery.of(parts);
    }

    /**
     * Decodes all encoded characters in the provided String.
     *
     * @param string Escaped string
     * @return string Unescaped string
     */
    public static String unescape(String string) {
        return StringEscapeUtils.unescapeJava(string);
    }

    /**
     * Build a DataView from provided JSON.
     * @param json JsonObject
     * @return DataView
     */
    public static DataView dataViewFromJson(JsonObject json) {
        DataContainer data = DataContainer.createNew();
        for (Entry<String, JsonElement> entry : json.entrySet()) {
            Optional<Object> value = jsonElementToObject(entry.getValue());
            if (value.isPresent()) {
                data.set(unescapeQuery(entry.getKey()), value.get());
            } else {
                Prism.getInstance().getLogger().error(String.format("Failed to transform %s data.", entry.getKey()));
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
        if (element.isJsonArray()) {
            List<Object> list = Lists.newArrayList();
            JsonArray jsonArray = element.getAsJsonArray();
            jsonArray.forEach(entry -> jsonElementToObject(entry).ifPresent(list::add));
            return Optional.of(list);
        } else if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            PrimitiveArray primitiveArray = PrimitiveArray.of(jsonObject);
            if (primitiveArray != null) {
                return Optional.of(primitiveArray.getArray());
            }

            return Optional.of(dataViewFromJson(jsonObject));
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
            if (jsonPrimitive.isBoolean()) {
                return Optional.of(jsonPrimitive.getAsBoolean());
            } else if (jsonPrimitive.isNumber()) {
                Number number = NumberUtils.createNumber(jsonPrimitive.getAsString());
                if (number instanceof Byte) {
                    return Optional.of(number.byteValue());
                } else if (number instanceof Double) {
                    return Optional.of(number.doubleValue());
                } else if (number instanceof Float) {
                    return Optional.of(number.floatValue());
                } else if (number instanceof Integer) {
                    return Optional.of(number.intValue());
                } else if (number instanceof Long) {
                    return Optional.of(number.longValue());
                } else if (number instanceof Short) {
                    return Optional.of(number.shortValue());
                }
            } else if (jsonPrimitive.isString()) {
                return Optional.of(jsonPrimitive.getAsString());
            }
        }

        return Optional.empty();
    }

    /**
     * Converts a DataView object into a JsonObject.
     *
     * @param view DataView
     * @return JsonObject JsonObject representation of the DataView
     */
    public static JsonObject jsonFromDataView(DataView view) {
        JsonObject jsonObject = new JsonObject();
        Gson gson = new GsonBuilder().create();

        Set<DataQuery> keys = view.getKeys(false);
        for (DataQuery query : keys) {

            String key = escapeQuery(query);
            Object value = view.get(query).orElse(null);

            if (value == null) {
                // continue
            } else if (value instanceof Collection) {
                List<Object> convertedList = Lists.newArrayList();
                for (Object object : (Collection<?>) value) {
                    if (object == null) {
                        // continue
                    } else if (object instanceof Collection) {
                        convertedList.add(gson.toJsonTree(object));
                    } else if (object instanceof DataView) {
                        convertedList.add(jsonFromDataView((DataView) object));
                    } else if (DataUtil.isPrimitiveType(object)) {
                        convertedList.add(gson.toJsonTree(object));
                    } else if (object.getClass().isArray()) {
                        convertedList.add(gson.toJsonTree(new PrimitiveArray(object)));
                    } else if (object.getClass().isEnum()) {
                        // convertedList.add(object.toString());
                    } else {
                        Prism.getInstance().getLogger().error("Unsupported json list data type: " + object.getClass().getName() + " for key " + key);
                    }

                    if (!convertedList.isEmpty()) {
                        jsonObject.add(key, gson.toJsonTree(convertedList));
                    }
                }
            } else if (value instanceof Boolean) {
                jsonObject.addProperty(key, (Boolean) value);
            } else if (value instanceof Character) {
                jsonObject.addProperty(key, (Character) value);
            } else if (value instanceof DataView) {
                jsonObject.add(key, jsonFromDataView((DataView) value));
            } else if (value instanceof Number) {
                jsonObject.addProperty(key, (Number) value);
            } else if (value instanceof String) {
                jsonObject.addProperty(key, (String) value);
            } else if (value.getClass().isArray()) {
                jsonObject.add(key, gson.toJsonTree(new PrimitiveArray(value)));
            } else {
                // Prism.getInstance().getLogger().error("Unsupported json data type: " + value.getClass().getName() + " for key " + key);
            }
        }

        return jsonObject;
    }

    /**
     * Helper method to translate Player UUIDs to names.
     *
     * @param results List of results
     * @param uuidsPendingLookup Lists of UUIDs pending lookup
     * @return CompletableFuture
     */
    public static CompletableFuture<List<Result>> translateUuidsToNames(List<Result> results, List<UUID> uuidsPendingLookup) {
        CompletableFuture<List<Result>> future = new CompletableFuture<>();

        CompletableFuture<Collection<GameProfile>> futures = Sponge.getServer().getGameProfileManager().getAllById(uuidsPendingLookup, true);
        futures.thenAccept((profiles) -> {
            for (GameProfile profile : profiles) {
                for (Result r : results) {
                    Optional<Object> cause = r.data.get(DataQueries.Cause);
                    if (cause.isPresent() && cause.get().equals(profile.getUniqueId().toString())) {
                        r.data.set(DataQueries.Cause, profile.getName().orElse("unknown"));
                    }
                }
            }

            future.complete(results);
        });

        return future;
    }

    /**
     * Helper method for writing values to a {@link DataView DataView}.
     *
     * @param dataView DataView
     * @param path DataQuery
     * @param value Object
     * @throws IllegalArgumentException If an attempt is made to change an existing value.
     */
    public static void writeToDataView(DataView dataView, DataQuery path, Object value) throws IllegalArgumentException {
        Preconditions.checkNotNull(dataView);
        Preconditions.checkNotNull(path);

        Object currentValue = dataView.get(path).orElse(null);
        if (currentValue != null) {
            if (!currentValue.equals(value)) {
                throw new IllegalArgumentException("Attempted to overwrite " + path.toString());
            }

            Prism.getInstance().getLogger().warn("Attempted to overwrite {} with the same value", path.toString(), new Exception());
            return;
        }

        dataView.set(path, value);
    }
}