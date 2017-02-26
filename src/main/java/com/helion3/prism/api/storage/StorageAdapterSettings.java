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

import java.util.UUID;

public interface StorageAdapterSettings {
    /**
     * Remove a meta setting by its key
     *
     * @param key The key of the setting to delete
     */
    void deleteSetting(String key);

    /**
     * Retrieve a meta setting by its key
     *
     * @param key The key of the setting to get
     * @return The setting value
     */
    String getSetting(String key);

    /**
     * Save a new key/value
     *
     * @param key The key of the setting to save
     * @param value The value to associate with the key
     */
    void saveSetting(String key, String value);

    /**
     * Remove an owner's meta setting by its key
     *
     * @param key The key of setting to delete
     * @param owner The owner which the setting belongs to
     */
    void deleteSetting(String key, UUID owner);

    /**
     * Save a new key/value for an owner
     *
     * @param key The key of the setting to save
     * @param value The value to associate with the key
     * @param owner The owner which the setting belongs to
     */
    void saveSetting(String key, String value, UUID owner);

    /**
     * Retrieve an owner's meta setting by its key
     *
     * @param key The key of the setting to get
     * @param owner The owner which the setting belongs to
     * @return The setting value
     */
    String getSetting(String key, UUID owner);
}
