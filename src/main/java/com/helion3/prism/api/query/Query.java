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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.helion3.prism.Prism;
import com.helion3.prism.api.parameters.ParameterHandler;

final public class Query {
    private boolean isAggregate = true;
    private List<Condition> conditions = new ArrayList<Condition>();

    /**
     * Builds a {@link Query} by parsing a string of parameters
     * and their values.
     *
     * @param parameters String Parameter: value string
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromParameters(QuerySession session, String parameters) {
        return fromParameters(session, parameters.split(" "));
    }

    /**
     * Builds a {@link Query} by parsing an array of parameters
     * and their values.
     *
     * @param parameters String[] Parameter:value list
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromParameters(QuerySession session, String[] parameters) {
        checkNotNull(parameters);
        checkNotNull(session);

        Query query = new Query();
        CompletableFuture<Query> future = new CompletableFuture<Query>();

        if (parameters.length > 0) {
            List<ListenableFuture<?>> futures = new ArrayList<ListenableFuture<?>>();
            for (String parameter : parameters) {
                // Determine the true alias and value
                String alias;
                String value;
                if (parameter.contains(":")) {
                    // Split the parameter: values
                    String[] split = parameter.split( ":", 2 );
                    alias = split[0];
                    value = split[1];
                } else {
                    // Any value with a defined parameter is assumed to be a player username.
                    alias = "p";
                    value = parameter;
                }

                // Simple validation
                if (alias.length() <= 0 || value.length() <= 0) {
                    // @todo throw invalid syntax error
                    break;
                }

                // Find a handler
                Optional<ParameterHandler> optionalHandler = Prism.getHandlerForParameter(alias);
                if (!optionalHandler.isPresent()) {
                    // @todo throw invalid alias error
                    break;
                }

                ParameterHandler handler = optionalHandler.get();

                // Allows this command source?
                if (!handler.acceptsSource(session.getCommandSource().get())) {
                    // @todo throw error
                    break;
                }

                // Validate value
                if (!handler.acceptsValue(value)) {
                    // @todo throw syntax error
                    break;
                }

                Optional<ListenableFuture<?>> listenable = handler.process(session, alias, value, query);

                if (listenable.isPresent()) {
                    futures.add(listenable.get());
                }
            }

            if (!futures.isEmpty()) {
                ListenableFuture<List<Object>> combinedFuture = Futures.allAsList(futures);
                combinedFuture.addListener(new Runnable() {
                    @Override
                    public void run() {
                        future.complete(query);
                    }
                }, MoreExecutors.sameThreadExecutor());
            } else {
                future.complete(query);
            }
        } else {
            future.cancel(false);
        }

        return future;
    }

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
}