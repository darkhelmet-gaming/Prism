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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.data.DataQuery;

import com.google.common.collect.Range;
import com.helion3.prism.Prism;
import com.helion3.prism.util.TypeUtil;

/**
 * Super simple SQL query builder.
 */
public class SQLQuery {
    private final static String tablePrefix = Prism.getConfig().getNode("db", "mysql", "tablePrefix").getString();
    private final String query;

    /**
     * Supported SQL query modes.
     */
    public enum Mode {
        SELECT
    }

    /**
     * Create a new SQLQuery with a query stirng.
     * @param query String query
     */
    public SQLQuery(String query) {
        this.query = query;
    }

    /**
     * Build an SQL query dynamically.
     */
    public static class Builder {
        private Mode mode;
        private List<String> columns = new ArrayList<>();
        private List<String> groupBy = new ArrayList<>();
        private List<String> unhexCols = new ArrayList<>();
        private String table;
        private Map<String, String> joins = new HashMap<>();
        private List<Condition> conditions = new ArrayList<>();
        private Map<DataQuery, QueryValueMutator> valueMutators = new HashMap<>();

        public Builder select() {
            mode = Mode.SELECT;
            return this;
        }

        /**
         * Add a column.
         *
         * @param col String column name.
         * @return Builder
         */
        public Builder col(String col) {
            columns.add(col);
            return this;
        }

        /**
         * Set the "from" table.
         *
         * @param table String table name
         * @return Builder
         */
        public Builder from(String table) {
            this.table = table;
            return this;
        }

        /**
         * Join another table.
         *
         * @param table String table name
         * @param on String condition
         * @return Builder
         */
        public Builder leftJoin(String table, String on) {
            joins.put(table, on);
            return this;
        }

        /**
         * Group by any number of columns. Will automatically add
         * the column name to the selected column list.
         *
         * @param cols String... column name(s)
         * @return Builder
         */
        public Builder group(String... cols) {
            for (String col : cols) {
                groupBy.add(col);
                columns.add(col);
            }
            return this;
        }

        /**
         * HEX the result of a column.
         *
         * @param cols String... column name(s)
         * @return Builder
         */
        public Builder hex(String... cols) {
            for (String col : cols) {
                columns.remove(col);
                unhexCols.add(col.toLowerCase());
                columns.add(String.format("HEX(%s) AS %1$sHexed", col));
            }

            return this;
        }

        /**
         * Add field/group conditions.
         *
         * @param conditions List<Condition>
         * @return Builder
         */
        public Builder conditions(List<Condition> conditions) {
            this.conditions.addAll(conditions);
            return this;
        }

        /**
         * Add a value mutator.
         *
         * @param path DataQuery
         * @param mutator QueryValueMutator mutator
         * @return Builder
         */
        public Builder valueMutator(DataQuery path, QueryValueMutator mutator) {
            valueMutators.put(path, mutator);
            return this;
        }

        /**
         * Build the final query string.
         *
         * @return String
         */
        public SQLQuery build() {
            String sql = mode.name() + " ";

            // Columns
            sql += String.join(", ", columns) + " ";

            // Tables
            sql += "FROM " + table + " ";

            // Joins
            for (Entry<String, String> entry : joins.entrySet()) {
                sql += "LEFT JOIN " + entry.getKey() + " ON " + entry.getValue() + " ";
            }

            // Where
            List<String> queryConditions = buildConditions(conditions);
            if (!queryConditions.isEmpty()) {
                sql += "WHERE " + String.join(" ", queryConditions).replaceFirst("AND ", "").replaceFirst("OR ", "") + " ";
            }

            // Group By
            if (!groupBy.isEmpty()) {
                sql += "GROUP BY " + String.join(", ", groupBy) + " ";
            }

            return new SQLQuery(sql);
        }

        /**
         * Recursive method to build AND/OR condition groups.
         * @todo Needs some more work, tests
         *
         * @param conditions List<Condition>
         * @return List of String conditions to append to a query.
         */
        private List<String> buildConditions(List<Condition> conditions) {
            List<String> queryConditions = new ArrayList<>();
            for (Condition fieldOrGroup : conditions) {
                if (fieldOrGroup instanceof ConditionGroup) {
                    ConditionGroup group = (ConditionGroup) fieldOrGroup;

                    String query = "AND (";

                    List<String> inner = new ArrayList<>();
                    for (Object obj : group.getConditions()) {
                        if (obj instanceof ConditionGroup) {
                            query += buildConditions(((ConditionGroup) obj).getConditions());
                        }
                        else if (obj instanceof FieldCondition) {
                            FieldCondition condition = (FieldCondition) obj;
                            String fieldComparator = getFieldComparator(condition);

                            if (!fieldComparator.isEmpty()) {
                                inner.add(popDataQuery(condition.getFieldName().toString()) + " " + fieldComparator);
                            }
                        }
                        // @todo need and/or here
                    }

                    if (!inner.isEmpty()) {
                        query += String.join(group.getOperator().name() + " ", inner);
                    }

                    queryConditions.add(query + ")");
                } else {
                    FieldCondition condition = (FieldCondition) fieldOrGroup;
                    String fieldComparator = getFieldComparator(condition);

                    if (!fieldComparator.isEmpty()) {
                        queryConditions.add("AND (" + popDataQuery(condition.getFieldName().toString()) + " " + fieldComparator + ")");
                    }
                }
            }

            return queryConditions;
        }

        /**
         * Builder a specific field comparator query fragment.
         *
         * @param condition FieldCondition
         * @return String
         */
        private String getFieldComparator(FieldCondition condition) {
            String fieldComparator = "";
            String field = popDataQuery(condition.getFieldName().toString());

            String value = "'" + condition.getValue().toString() + "'";
            QueryValueMutator mutator = valueMutators.get(condition.getFieldName());
            if (mutator != null) {
                value = mutator.mutate(condition.getValue().toString());
            }

            if (condition.getMatchRule().equals(MatchRule.EQUALS)) {
                fieldComparator += "= " + value + " ";
            }
            else if (condition.getMatchRule().equals(MatchRule.BETWEEN)) {
                Range<?> range = (Range<?>) condition.getValue();
                fieldComparator += "> " + range.lowerEndpoint() + " AND " + field + " < " + range.upperEndpoint() + " ";
            }
            else if (condition.getMatchRule().equals(MatchRule.GREATER_THAN_EQUAL)) {
                fieldComparator += ">= " + condition.getValue() + " ";
            }
            else if (condition.getMatchRule().equals(MatchRule.LESS_THAN_EQUAL)) {
                fieldComparator += "<= " + condition.getValue() + " ";
            }
            // @todo handle includes, excludes

            return fieldComparator;
        }

        // @todo Pending DataQuery.last in sponge
        private String popDataQuery(String query) {
            String[] split = query.split("\\.");
            return split[split.length - 1];
        }
    }

    /**
     * Creates a new builder.
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Constructs an SQL query from a given QuerySession.
     *
     * @param session QuerySession
     * @return SQLQuery
     */
    public static SQLQuery from(QuerySession session) {
        Builder query = SQLQuery.builder().select().from(tablePrefix + "records");
        if (session.getQuery().isAggregate()) {
            query.col("COUNT(*) AS total").group("eventName", "target", "player", "cause");
        } else {
            query.col("*").leftJoin(tablePrefix + "extra", tablePrefix + "records.id = " + tablePrefix + "extra.record_id");
        }

        query.conditions(session.getQuery().getConditions());

        return query.build();
    }

    @Override
    public String toString() {
        return query;
    }
}
