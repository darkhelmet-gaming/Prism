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

import org.spongepowered.api.command.CommandSource;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.parameters.ParameterException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

/**
 * Represents a record query session, which includes the actual
 * {@link Query} as well as the source, and result set meta data.
 */
public class QuerySession {
    protected List<Flag> flags = new ArrayList<>();
    protected final CommandSource commandSource;
    protected Query query;
    protected int radius;
    protected Sort sort = Sort.NEWEST_FIRST;

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
     * @param commandSource The {@link CommandSource} this session belongs to
     */
    public QuerySession(CommandSource commandSource) {
        this.commandSource = commandSource;
    }

    /**
     * Add a flag to this query session.
     *
     * @param flag The {@link Flag} to add
     */
    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    /**
     * Returns the command source this session belongs to, if any.
     *
     * @return The {@link CommandSource} of this session, if available
     */
    public Optional<CommandSource> getCommandSource(){
        return Optional.ofNullable(commandSource);
    }

    /**
     * Get the query defined for this session.
     *
     * @return The {@link Query} for this session
     */
    public Query getQuery(){
        return query;
    }

    /**
     * Check if a given flag is present.
     *
     * @param flag Flag
     * @return True if the query has the specified flag
     */
    public boolean hasFlag(Flag flag) {
        return flags.contains(flag);
    }

    /**
     * Create a new Query for this session.
     *
     * @return The newly constructed {@link Query}
     */
    public Query newQuery() {
        query = new Query();
        return query;
    }

    /**
     * For reference reasons, we need to know whether this was
     * limited to a radius around the player (if any).
     *
     * @return The radius around the player
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Set the radius used for the query.
     *
     * @param radius The radius the query should use
     */
    public void setRadius(int radius) {
        this.radius = radius;
    }

    /**
     * Create a new {@link Query} from the given parameters.
     *
     * @param arguments The string parameters/flags
     * @return The {@link CompletableFuture} of the query
     * @throws ParameterException If the flag cannot be parsed from the flag argument
     */
    public CompletableFuture<Void> newQueryFromArguments(@Nullable String arguments) throws ParameterException {
        CompletableFuture<Query> future = QueryBuilder.fromArguments(this, arguments);
        return future.thenAccept(query -> this.query = query);
    }

    /**
     * Sets the sort order to be used for the query.
     *
     * @param sort The desired sort order
     */
    public void setSortBy(Sort sort) {
        this.sort = sort;
    }

    /**
     * Gets the sort order to be used for the query.
     *
     * @return The sort order
     */
    public Sort getSortBy() {
        return sort;
    }
}