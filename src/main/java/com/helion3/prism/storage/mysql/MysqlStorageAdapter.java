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
package com.helion3.prism.storage.mysql;

import com.helion3.prism.api.actions.ActionHandler;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.storage.StorageAdapter;

import java.util.ArrayList;
import java.util.List;

public class MysqlStorageAdapter implements StorageAdapter {

    /**
     * Establish connections to the database
     *
     * @return Whether we could connect properly
     */
    // @todo implement
    @Override
    public boolean connect() throws Exception {

        return false;

    }

    /**
     * Execute a query session, for a list of resulting actions
     *
     * @param session
     * @return List of {@link com.helion3.prism.api.actions.ActionHandler}
     */
    // @todo implement
    @Override
    public List<ActionHandler> query( QuerySession session ) throws Exception {
        List<ActionHandler> handlers = new ArrayList<ActionHandler>();
        return handlers;
    }

    /**
     * Prism purges records in chunks. How we find those chunks is determined
     * by the storage engine. For MySQL it's by primary key, etc.
     *
     * It must be numerical (something we can increment), largest allowed is a long.
     *
     * @return
     */
    // @todo implement
    @Override
    public long getMinimumChunkingKey(){
        return 0;
    }
    // @todo implement
    @Override
    public long getMaximumChunkingKey(){
        return 0;
    }

    /**
     * Given a list of parameters, will remove all matching records.
     *
     * @param query Query conditions indicating what we're purging
     * @return
     */
    // @todo implement
    @Override
    public int delete(Query query){
        return 0;
    }

    /**
     * Close connections.
     */
    // @todo implement
    @Override
    public void close(){

    }

    /**
     * Test the connection, returns true if valid and ready, false
     * if error/null.
     *
     * @return
     * @throws Exception If connection fails
     */
    // @todo implement
    @Override
    public boolean testConnection() throws Exception {
        return false;
    }
}
