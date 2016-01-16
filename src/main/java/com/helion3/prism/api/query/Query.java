/**
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
    private boolean isAggregate = true;
    private List<Condition> conditions = new ArrayList<Condition>();
    private int limit = 5;

    /**
     * Add a condition.
     *
     * @param condition Condition
     */
    public void addCondition(Condition condition) {
        conditions.add(condition);
    }

    /**
     * Add a list of conditions.
     *
     * @param conditions List of conditions.
     */
    public void addConditions(List<Condition> conditions) {
        this.conditions.addAll(conditions);
    }

    /**
     *
     * @return
     */
    public List<Condition> getConditions() {
        return conditions;
    }

    /**
     * Returns whether records will be aggregated/grouped.
     *
     * @return boolean
     */
    public boolean isAggregate() {
        return isAggregate;
    }

    /**
     * Toggle aggregate records or complete records.
     * @param isAggregate boolean Toggle aggregate or complete records
     */
    public void setAggregate(boolean isAggregate) {
        this.isAggregate = isAggregate;
    }

    /**
     * Get the record limit.
     *
     * @return Record limit.
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Set the record limit.
     *
     * @param limit int Record limit
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }
}