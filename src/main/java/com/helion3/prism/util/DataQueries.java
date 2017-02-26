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

import static org.spongepowered.api.data.DataQuery.of;

import org.spongepowered.api.data.DataQuery;

public class DataQueries {
    private DataQueries() {}

    public static final DataQuery BlockType = of("BlockType");
    public static final DataQuery BlockState = of("BlockState");
    public static final DataQuery Cause = of("Cause");
    public static final DataQuery ContentVersion = of("ContentVersion");
    public static final DataQuery Count = of("Count");
    public static final DataQuery Created = of("Created");
    public static final DataQuery Entity = of("Entity");
    public static final DataQuery EntityType = of("EntityType");
    public static final DataQuery EventName = of("EventName");
    public static final DataQuery id = of("Id");
    public static final DataQuery Location = of("Location");
    public static final DataQuery OriginalBlock = of("Original");
    public static final DataQuery Player = of("Player");
    public static final DataQuery Position = of("Position");
    public static final DataQuery Quantity = of("Quantity");
    public static final DataQuery ReplacementBlock = of("Replacement");
    public static final DataQuery Rotation = of("Rotation");
    public static final DataQuery Target = of("Target");
    public static final DataQuery UnsafeData = of("UnsafeData");
    public static final DataQuery WorldName = of("WorldName");
    public static final DataQuery WorldUuid = of("WorldUuid");
    public static final DataQuery X = of("X");
    public static final DataQuery Y = of("Y");
    public static final DataQuery Z = of("Z");

    // Pending cleanup from Sponge?
    public static final DataQuery Pos = of("Pos");
}
