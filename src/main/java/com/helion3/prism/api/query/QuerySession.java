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

import org.spongepowered.api.command.CommandSource;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Represents a record query session, which includes the actual
 * {@link Query} as well as the source, and result set meta data.
 *
 */
public class QuerySession {
    protected Query query;
    protected final CommandSource commandSource;

    /**
     * Constructs a new session without any command source.
     */
    public QuerySession() {
        this.commandSource = null;
    }

    /**
     * Constructs a new query session with a known command source.
     *
     * Queries may behave differently based on a user - for example a Player
     * may lookup a radius but a console command or API call has no location.
     *
     * @param commandSource CommandSource this session belongs to.
     */
    public QuerySession(CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    /**
     * Returns the command source this session belongs to, if any.
     *
     * @return CommandSource
     */
    public Optional<CommandSource> getCommandSource(){
        return Optional.of(commandSource);
    }

    /**
     * Get the query defined for this session.
     *
     * @return {@link Query}
     */
    public Query getQuery(){
        return query;
    }

    /**
     * Create a new Query for this session.
     *
     * @return Query
     */
    public Query newQuery() {
        query = new Query();
        return query;
    }

    /**
     * Create a new Query from the given parameters.
     *
     * @param parameters String parameters
     * @return CompletableFuture<Query>
     */
    public CompletableFuture<Query> newQueryFromParameters(String parameters) {
        CompletableFuture<Query> future = Query.fromParameters(this, parameters);
        future.thenAccept(query -> {
            this.query = query;
        });

        return future;
    }
}