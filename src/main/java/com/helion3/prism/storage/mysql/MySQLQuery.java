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
package com.helion3.prism.storage.mysql;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.SQLQuery;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.TypeUtil;

public class MySQLQuery extends SQLQuery {
    public MySQLQuery(String query) {
        super(query);
    }

    /**
     * Constructs a MySQL SQL query from a given QuerySession.
     *
     * @param session QuerySession
     * @return SQLQuery
     */
    public static SQLQuery from(QuerySession session) {
        Builder builder = SQLQuery.builder().select().from(tablePrefix + "records AS r");
        if (!session.hasFlag(Flag.NO_GROUP)) {
            builder.group(
                    DataQueries.EventName.toString(),
                    DataQueries.Target.toString(),
                    DataQueries.Player.toString(),
                    DataQueries.Cause.toString(),
                    "DATE_FORMAT(created, '%Y-%m-%d')"
            ).col("COUNT(*) AS total").col("DATE_FORMAT(created, '%Y-%m-%d') as created");
        } else {
            builder.col("*").leftJoin(tablePrefix + "extra AS e", "r.id = e.record_id");
        }

        builder.hex(DataQueries.Player.toString(), DataQueries.WorldUuid.toString()).conditions(session.getQuery().getConditions());
        builder.valueMutator(DataQueries.Player, value -> "UNHEX('" + TypeUtil.uuidStringToDbString(value) + "')");
        builder.valueMutator(DataQueries.Location.then(DataQueries.WorldUuid), value -> "UNHEX('" + TypeUtil.uuidStringToDbString(value) + "')");

        // Get Sorting order.
        builder.order("created " + session.getSortBy().getString());

        return builder.build();
    }
}
