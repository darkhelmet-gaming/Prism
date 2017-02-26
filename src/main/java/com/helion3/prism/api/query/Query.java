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

import java.util.ArrayList;
import java.util.List;

final public class Query {
    private List<Condition> conditions = new ArrayList<>();
    private int limit = 1000;

    public void addConditions(List<Condition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     * Convenience method to add a single AND condition.
     *
     * @param condition The {@link Condition} to add to the query
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Get a list of all conditions in the query
     *
     * @return All conditions within the query
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * Get the record limit.
     *
     * @return The limit of records
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the record limit.
     *
     * @param limit The desired result limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}