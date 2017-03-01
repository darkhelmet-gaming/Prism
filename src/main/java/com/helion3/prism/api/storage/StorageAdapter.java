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

public interface StorageAdapter {
    /**
     * Establishes an active connection to the storage engine. Any DB
     * setup/initialization code will run here.
     *
     * @return Whether or not we connected properly
     * @throws Exception Abstract DB exceptions
     */
    boolean connect() throws Exception;

    /**
     * Holder for the storage adapter handling
     * event records.
     * @return StorageAdapterRecords Storage adapter for records
     */
    StorageAdapterRecords records();

    /**
     * Holder for the storage adapter handling
     * per-player settings.
     * @return StorageAdapterSettings Storage adapter for settings
     */
    StorageAdapterSettings settings();

    /**
     * Close connections.
     */
    void close();

    /**
     * Test the connection, returns true if valid and ready, false if
     * error/null.
     *
     * @return
     * @throws Exception If connection fails
     */
    boolean testConnection() throws Exception;
}