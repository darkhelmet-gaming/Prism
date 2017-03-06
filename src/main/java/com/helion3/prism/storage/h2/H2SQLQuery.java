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
package com.helion3.prism.storage.h2;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.SQLQuery;

public class H2SQLQuery extends SQLQuery {
    public H2SQLQuery(String query) {
        super(query);
    }

    /**
     * Constructs an H2 SQL query from a given QuerySession.
     *
     * @param session QuerySession
     * @return SQLQuery
     */
    public static SQLQuery from(QuerySession session) {
        Builder query = SQLQuery.builder().select().from(tablePrefix + "records AS r");
        if (!session.hasFlag(Flag.NO_GROUP)) {
            query.col("COUNT(*) AS total");
            query.group("eventName", "target", "player", "cause");
        } else {
            query.col("*").leftJoin(tablePrefix + "extra AS e", "r.id = e.record_id");
        }

        query.conditions(session.getQuery().getConditions());

        // Sort by timestamp if we're not grouping
        if (session.hasFlag(Flag.NO_GROUP)) {
            query.order("created " + session.getSortBy().getString());
        }

        return query.build();
    }
}
