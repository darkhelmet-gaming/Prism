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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class PrimitiveArrayTest {

    private static final byte[] BYTE_ARRAY = new byte[]{Byte.MIN_VALUE, 0, Byte.MAX_VALUE};
    private static final int[] INT_ARRAY = new int[]{Integer.MIN_VALUE, 0, Integer.MAX_VALUE};
    private static final long[] LONG_ARRAY = new long[]{Long.MIN_VALUE, 0, Long.MAX_VALUE};

    @Test
    public void testByteArrayFromByteArray() {
        PrimitiveArray primitiveArray = new PrimitiveArray(BYTE_ARRAY);

        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.BYTE_ARRAY_ID);
        Assertions.assertArrayEquals(BYTE_ARRAY, (byte[]) primitiveArray.getArray());
    }

    @Test
    public void testByteArrayFromJsonObject() {
        JsonArray jsonArray = new JsonArray();
        for (byte number : BYTE_ARRAY) {
            jsonArray.add(number);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", PrimitiveArray.BYTE_ARRAY_ID);
        jsonObject.add("value", jsonArray);

        PrimitiveArray primitiveArray = PrimitiveArray.of(jsonObject);

        Assertions.assertNotNull(primitiveArray);
        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.BYTE_ARRAY_ID);
        Assertions.assertArrayEquals(BYTE_ARRAY, (byte[]) primitiveArray.getArray());
    }

    @Test
    public void testIntArrayFromIntArray() {
        PrimitiveArray primitiveArray = new PrimitiveArray(INT_ARRAY);

        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.INT_ARRAY_ID);
        Assertions.assertArrayEquals(INT_ARRAY, (int[]) primitiveArray.getArray());
    }

    @Test
    public void testIntArrayFromJsonObject() {
        JsonArray jsonArray = new JsonArray();
        for (int number : INT_ARRAY) {
            jsonArray.add(number);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", PrimitiveArray.INT_ARRAY_ID);
        jsonObject.add("value", jsonArray);

        PrimitiveArray primitiveArray = PrimitiveArray.of(jsonObject);

        Assertions.assertNotNull(primitiveArray);
        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.INT_ARRAY_ID);
        Assertions.assertArrayEquals(INT_ARRAY, (int[]) primitiveArray.getArray());
    }

    @Test
    public void testLongArrayFromLongArray() {
        PrimitiveArray primitiveArray = new PrimitiveArray(LONG_ARRAY);

        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.LONG_ARRAY_ID);
        Assertions.assertArrayEquals(LONG_ARRAY, (long[]) primitiveArray.getArray());
    }

    @Test
    public void testLongArrayFromJsonObject() {
        JsonArray jsonArray = new JsonArray();
        for (long number : LONG_ARRAY) {
            jsonArray.add(number);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("key", PrimitiveArray.LONG_ARRAY_ID);
        jsonObject.add("value", jsonArray);

        PrimitiveArray primitiveArray = PrimitiveArray.of(jsonObject);

        Assertions.assertNotNull(primitiveArray);
        Assertions.assertEquals(primitiveArray.getKey(), PrimitiveArray.LONG_ARRAY_ID);
        Assertions.assertArrayEquals(LONG_ARRAY, (long[]) primitiveArray.getArray());
    }
}