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
package com.helion3.prism.api.query;

public enum Sort {
    // Set sorting values for MongoDB/MySQL/H2 sorting.
    NEWEST_FIRST(-1, "DESC"),
    OLDEST_FIRST(1, "ASC");

    private int sortVal;
    private String sortString;

    Sort(int sortVal, String sortString) {
        this.sortVal = sortVal;
        this.sortString = sortString;
    }

    /**
     * Gets the value for sorting order for MongoDB.
     *
     * @return The sorting value int
     */
    public int getValue() {
        return sortVal;
    }

    /**
     * Gets the sorting order to be used in H2/MySQL.
     *
     * @return The sorting order string
     */
    public String getString() {
        return sortString;
    }
}
