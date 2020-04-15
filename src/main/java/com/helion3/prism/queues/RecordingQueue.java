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
package com.helion3.prism.queues;

import java.util.concurrent.LinkedBlockingQueue;

import com.helion3.prism.api.records.PrismRecord;
import org.spongepowered.api.data.DataContainer;

public class RecordingQueue {

    private static final LinkedBlockingQueue<PrismRecord> queue = new LinkedBlockingQueue<>();

    private RecordingQueue(){}

    /**
     * Adds a new Event to the recording queue.
     *
     * @param record Event to be queued for database write
     */
    public static void add(final PrismRecord record) {
        if (record == null) {
            throw new IllegalArgumentException("null PrismRecord given to Prism recording queue");
        }

        if (record.getDataContainer() == null) {
            throw new IllegalArgumentException("PrismRecord with null container given to Prism recording queue.");
        }

        queue.add(record);
    }

    /**
     * Returns all unsaved events in the queue.
     *
     * @return Current unsaved {@link DataContainer} queue
     */
    public static LinkedBlockingQueue<PrismRecord> getQueue() {
        return queue;
    }
}