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
package com.helion3.prism.utils;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.helion3.prism.Prism;

public class DataUtils {
    private DataUtils() {}

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
     * Converts a DataView object into a JsonObject.
     *
     * @param view DataView
     * @return JsonObject JsonObject representation of the DataView
     */
    public static JsonObject jsonFromDataView(DataView view) {
        JsonObject json = new JsonObject();

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
                            json.add(key, jsonFromDataView((DataView) object));
                        }
                        else if (DataUtils.isPrimitiveType(object)) {
                            Gson gson = new GsonBuilder().create();
                            JsonArray array = gson.toJsonTree(optional.get()).getAsJsonArray();
                            json.add(key, array);
                            break;
                        }
                        else {
                            Prism.getLogger().error("Unsupported list data type: " + object.getClass().getName());
                        }
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
}
