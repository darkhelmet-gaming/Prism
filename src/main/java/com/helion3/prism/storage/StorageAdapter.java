/**
 * This file is part of Prism, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2014 Helion3 http://helion3.com/
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
package com.helion3.prism.storage;

import com.helion3.prism.api.actions.ActionHandler;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

import java.util.List;

public interface StorageAdapter {

    /**
     * Establishes an active connection to the storage
     * engine. Any DB setup/initialization code will run
     * here.
     *
     * // @todo needs config/wrapper for db server
     *
     * @return Whether or not we connected properly
     * @throws Exception Abstract DB exceptions
     */
    boolean connect() throws Exception;

    /**
     * Execute a query session, for a list of resulting actions
     *
     * @param session
     * @return List of {@link ActionHandler}
     * @throws Exception Abstract DB or query/handler exceptions
     */
    List<ActionHandler> query( QuerySession session ) throws Exception;

    /**
     * Prism purges records in chunks. How we find those chunks is determined
     * by the storage engine. For MySQL it's by primary key, etc.
     *
     * It must be numerical (something we can increment), largest allowed is a long.
     *
     * @return
     * @throws Exception Abstract DB or query/handler exceptions
     */
    long getMinimumChunkingKey() throws Exception;
    long getMaximumChunkingKey() throws Exception;

    /**
     * Given a list of parameters, will remove all matching records.
     *
     * @param query Query conditions indicating what we're purging
     * @return
     * @throws Exception Abstract DB or query/handler exceptions
     */
    int delete(Query query) throws Exception;

    /**
     * Close connections.
     */
    void close();

    /**
     * Test the connection, returns true if valid and ready, false
     * if error/null.
     *
     * @return
     * @throws Exception If connection fails
     */
    boolean testConnection() throws Exception;

}
