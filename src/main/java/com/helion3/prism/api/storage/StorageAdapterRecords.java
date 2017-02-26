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
package com.helion3.prism.api.storage;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.helion3.prism.api.records.Result;
import org.spongepowered.api.data.DataContainer;

import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

public interface StorageAdapterRecords {
    /**
     * Writes a collection of events to storage
     *
     * @param containers All {@link DataContainer}s to persist
     * @return The {@link StorageWriteResult} of the write
     * @throws Exception Abstract database exceptions
     */
    StorageWriteResult write(List<DataContainer> containers) throws Exception;

    /**
     * Execute a query session for a list of resulting actions
     *
     * @param session The {@link QuerySession}
     * @param translate Whether or not to translate player UUIDs to last known names
     * @return A completable future of a list of all {@link Result}
     * @throws Exception Abstract database or query/handler exceptions
     */
    CompletableFuture<List<Result>> query(QuerySession session, boolean translate) throws Exception;

    /**
     * Given a {@link Query} this will remove all matching records.
     *
     * @param query The {@link Query} conditions indicating what we're purging
     * @return The {@link StorageDeleteResult}
     * @throws Exception Abstract database or query/handler exceptions
     */
    StorageDeleteResult delete(Query query) throws Exception;
}
