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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.bson.Document;

import java.util.List;

/**
 * https://wiki.vg/NBT - NBT only supports Byte, Int and Long Arrays
 */
public class PrimitiveArray {

    public static final String BYTE_ARRAY_ID = String.format("%s:byte", Reference.ID);
    public static final String INT_ARRAY_ID = String.format("%s:int", Reference.ID);
    public static final String LONG_ARRAY_ID = String.format("%s:long", Reference.ID);
    private final String key;
    private final List<? extends Number> value;

    public PrimitiveArray(Object object) {
        Preconditions.checkArgument(object != null);
        Preconditions.checkArgument(object.getClass().isArray());

        if (object instanceof byte[]) {
            this.key = BYTE_ARRAY_ID;
            this.value = fromByteArray((byte[]) object);
        } else if (object instanceof int[]) {
            this.key = INT_ARRAY_ID;
            this.value = fromIntArray((int[]) object);
        } else if (object instanceof long[]) {
            this.key = LONG_ARRAY_ID;
            this.value = fromLongArray((long[]) object);
        } else {
            throw new UnsupportedOperationException("Unsupported primitive type");
        }
    }

    public PrimitiveArray(String key, List<? extends Number> value) {
        Preconditions.checkArgument(StringUtils.equalsAny(key, BYTE_ARRAY_ID, INT_ARRAY_ID, LONG_ARRAY_ID));
        this.key = key;
        this.value = value;
    }

    public static PrimitiveArray of(Document document) {
        if (document.size() != 2 || !(document.containsKey("key") && document.containsKey("value"))) {
            return null;
        }

        if (!(document.get("key") instanceof String)) {
            return null;
        }

        String key = document.getString("key");
        if (!StringUtils.equalsAny(key, BYTE_ARRAY_ID, INT_ARRAY_ID, LONG_ARRAY_ID)) {
            return null;
        }

        List<Number> value = document.getList("value", Number.class);
        if (value == null) {
            return null;
        }

        return new PrimitiveArray(key, value);
    }

    public static PrimitiveArray of(JsonObject jsonObject) {
        if (jsonObject.size() != 2 || !(jsonObject.has("key") && jsonObject.has("value"))) {
            return null;
        }

        if (!jsonObject.get("key").isJsonPrimitive() || !jsonObject.getAsJsonPrimitive("key").isString()) {
            return null;
        }

        String key = jsonObject.get("key").getAsString();
        if (!StringUtils.equalsAny(key, BYTE_ARRAY_ID, INT_ARRAY_ID, LONG_ARRAY_ID)) {
            return null;
        }

        List<Number> value = Lists.newArrayList();
        JsonArray jsonArray = jsonObject.get("value").getAsJsonArray();
        jsonArray.forEach(entry -> value.add(NumberUtils.createNumber(entry.getAsString())));
        return new PrimitiveArray(key, value);
    }

    public Object getArray() {
        if (StringUtils.equals(this.key, BYTE_ARRAY_ID)) {
            return toByteArray(this.value);
        } else if (StringUtils.equals(this.key, INT_ARRAY_ID)) {
            return toIntArray(this.value);
        } else if (StringUtils.equals(this.key, LONG_ARRAY_ID)) {
            return toLongArray(this.value);
        } else {
            throw new UnsupportedOperationException("Unsupported primitive type");
        }
    }

    private byte[] toByteArray(List<? extends Number> list) {
        byte[] array = new byte[list.size()];
        for (int index = 0; index < list.size(); index++) {
            array[index] = list.get(index).byteValue();
        }

        return array;
    }

    private List<Byte> fromByteArray(byte[] array) {
        List<Byte> list = Lists.newArrayList();
        for (int index = 0; index < array.length; index++) {
            list.add(index, array[index]);
        }

        return list;
    }

    private int[] toIntArray(List<? extends Number> list) {
        int[] array = new int[list.size()];
        for (int index = 0; index < list.size(); index++) {
            array[index] = list.get(index).intValue();
        }

        return array;
    }

    private List<Integer> fromIntArray(int[] array) {
        List<Integer> list = Lists.newArrayList();
        for (int index = 0; index < array.length; index++) {
            list.add(index, array[index]);
        }

        return list;
    }

    private long[] toLongArray(List<? extends Number> list) {
        long[] array = new long[list.size()];
        for (int index = 0; index < list.size(); index++) {
            array[index] = list.get(index).longValue();
        }

        return array;
    }

    private List<Long> fromLongArray(long[] array) {
        List<Long> list = Lists.newArrayList();
        for (int index = 0; index < array.length; index++) {
            list.add(index, array[index]);
        }

        return list;
    }

    public String getKey() {
        return key;
    }

    public List<?> getValue() {
        return value;
    }
}