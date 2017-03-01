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
package com.helion3.prism.api.parameters;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.command.CommandSource;

import com.google.common.collect.ImmutableList;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

public interface ParameterHandler {
    /**
     * Returns whether this parameter is allowed for the current
     * command source. Example: radius parameter only for Player.
     *
     * @param source CommandSource of current parameter.
     * @return boolean Whether this command source may use this parameter.
     */
    boolean acceptsSource(@Nullable CommandSource source);

    /**
     * Returns whether the given value(s) for the handler
     * are acceptable.
     *
     * @param value String Value/input for the parameter
     * @return boolean Whether this value is legal for this parameter.
     */
    boolean acceptsValue(String value);

    /**
     * Get all parameter aliases this handler applies to.
     *
     * @return List of aliases.
     */
    ImmutableList<String> getAliases();

    /**
     * Returns whether this handler responds to the given alias.
     *
     * @param alias String Alias to check against
     * @return boolean Whether this handler responds to an alias.
     */
    boolean handles(String alias);

    /**
     * Processes the given value into conditions which are then
     * appended to the query.
     *
     * @param session Current Query Session
     * @param parameter String parameter used
     * @param value String value(s) given with parameter
     * @param query Query Current query object
     */
    Optional<CompletableFuture<?>> process(QuerySession session, String parameter, String value, Query query);

    /**
     * Called when this handler's aliases were not defined.
     *
     * @param session QuerySession
     */
    default Optional<Pair<String, String>> processDefault(QuerySession session, Query query) {
        return Optional.empty();
    }
}
