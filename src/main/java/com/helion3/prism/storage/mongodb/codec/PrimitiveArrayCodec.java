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

package com.helion3.prism.storage.mongodb.codec;

import com.google.common.collect.Lists;
import com.helion3.prism.util.PrimitiveArray;
import org.apache.commons.lang3.StringUtils;
import org.bson.BsonInvalidOperationException;
import org.bson.BsonReader;
import org.bson.BsonType;
import org.bson.BsonWriter;
import org.bson.codecs.ByteCodec;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.IntegerCodec;
import org.bson.codecs.LongCodec;

import java.util.List;

public class PrimitiveArrayCodec implements Codec<PrimitiveArray> {

    private final ByteCodec byteCodec = new ByteCodec();
    private final IntegerCodec integerCodec = new IntegerCodec();
    private final LongCodec longCodec = new LongCodec();

    @Override
    public PrimitiveArray decode(BsonReader reader, DecoderContext decoderContext) {
        reader.readStartDocument();
        String key = reader.readString("key");

        List<Number> value = Lists.newArrayList();
        if (StringUtils.equals(key, PrimitiveArray.BYTE_ARRAY_ID)) {
            reader.readName("value");
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                value.add(byteCodec.decode(reader, decoderContext));
            }

            reader.readEndArray();
        } else if (StringUtils.equals(key, PrimitiveArray.INT_ARRAY_ID)) {
            reader.readName("value");
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                value.add(integerCodec.decode(reader, decoderContext));
            }

            reader.readEndArray();
        } else if (StringUtils.equals(key, PrimitiveArray.LONG_ARRAY_ID)) {
            reader.readName("value");
            reader.readStartArray();
            while (reader.readBsonType() != BsonType.END_OF_DOCUMENT) {
                value.add(longCodec.decode(reader, decoderContext));
            }

            reader.readEndArray();
        } else {
            reader.readEndDocument();
            throw new BsonInvalidOperationException("Unsupported primitive type");
        }

        reader.readEndDocument();
        return new PrimitiveArray(key, value);
    }

    @Override
    public void encode(BsonWriter writer, PrimitiveArray object, EncoderContext encoderContext) {
        writer.writeStartDocument();
        writer.writeString("key", object.getKey());

        Object array = object.getArray();
        if (array instanceof byte[]) {
            writer.writeName("value");
            writer.writeStartArray();
            for (byte value : (byte[]) array) {
                byteCodec.encode(writer, value, encoderContext);
            }

            writer.writeEndArray();
        } else if (array instanceof int[]) {
            writer.writeName("value");
            writer.writeStartArray();
            for (int value : (int[]) array) {
                integerCodec.encode(writer, value, encoderContext);
            }

            writer.writeEndArray();
        } else if (array instanceof long[]) {
            writer.writeName("value");
            writer.writeStartArray();
            for (long value : (long[]) array) {
                longCodec.encode(writer, value, encoderContext);
            }

            writer.writeEndArray();
        } else {
            writer.writeEndDocument();
            throw new BsonInvalidOperationException("Unsupported primitive type");
        }

        writer.writeEndDocument();
    }

    @Override
    public Class<PrimitiveArray> getEncoderClass() {
        return PrimitiveArray.class;
    }
}