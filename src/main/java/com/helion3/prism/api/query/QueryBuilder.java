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
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.helion3.prism.Prism;
import com.helion3.prism.api.flags.FlagHandler;
import com.helion3.prism.api.parameters.ParameterException;
import com.helion3.prism.api.parameters.ParameterHandler;

public class QueryBuilder {
    private QueryBuilder() {}
    private static final Pattern flagPattern = Pattern.compile("(-)([^\\s]+)?");

    /**
     * Return an empty query.
     * @return
     */
    public static Query empty() {
        return new Query();
    }

    /**
     * Builds a {@link Query} by parsing a string of arguments.
     *
     * @param parameters String Parameter: value string
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromArguments(QuerySession session, @Nullable String arguments) throws ParameterException {
        if (arguments == null) {
            CompletableFuture<Query> future = new CompletableFuture<Query>();
            future.complete(empty());

            return future;
        } else {
            return fromArguments(session, arguments.split(" "));
        }
    }

    /**
     * Builds a {@link Query} by parsing an array of arguments.
     *
     * @param parameters String[] Parameter:value list
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromArguments(QuerySession session, @Nullable String[] arguments) throws ParameterException {
        checkNotNull(session);

        Query query = new Query();
        CompletableFuture<Query> future = new CompletableFuture<Query>();

        if (arguments.length > 0) {
            List<ListenableFuture<?>> futures = new ArrayList<ListenableFuture<?>>();
            for (String arg : arguments) {
                Optional<ListenableFuture<?>> listenable;

                if (flagPattern.matcher(arg).matches()) {
                    listenable = parseFlagFromArgument(session, query, arg);
                } else {
                    listenable = parseParameterFromArgument(session, query, arg);
                }

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
     * Parses a flag argument.
     *
     * @param session QuerySession current session.
     * @param query Query query being built.
     * @param parameter String argument which should be a parameter
     * @return Optional<ListenableFuture<?>>
     */
    private static Optional<ListenableFuture<?>> parseFlagFromArgument(QuerySession session, Query query, String flag) throws ParameterException {
        flag = flag.substring(1);

        // Determine the true alias and value
        Optional<String> optionalValue = Optional.empty();
        if (flag.contains("=")) {
            // Split the parameter: values
            String[] split = flag.split("=");
            flag = split[0];
            if (!split[1].trim().isEmpty()) {
                optionalValue = Optional.of(split[1]);
            }
        }

        // Find a handler
        Optional<FlagHandler> optionalHandler = Prism.getFlagHandler(flag);
        if (!optionalHandler.isPresent()) {
            throw new ParameterException("\"" + flag + "\" is not a valid flag. No handler found.");
        }

        FlagHandler handler = optionalHandler.get();

        // Allows this command source?
        if (!handler.acceptsSource(session.getCommandSource().get())) {
            throw new ParameterException("This command source may not use the \"" + flag + "\" flag.");
        }

        // Validate value
        if (optionalValue.isPresent() && !handler.acceptsValue(optionalValue.get())) {
            throw new ParameterException("Invalid value \"" + optionalValue.get() + "\" for parameter \"" + flag + "\".");
        }

        return handler.process(session, flag, optionalValue, query);
    }

    /**
     * Parses a parameter argument.
     *
     * @param session QuerySession current session.
     * @param query Query query being built.
     * @param parameter String argument which should be a parameter
     * @return Optional<ListenableFuture<?>>
     * @throws ParameterException
     */
    private static Optional<ListenableFuture<?>> parseParameterFromArgument(QuerySession session, Query query, String parameter) throws ParameterException {
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
            throw new ParameterException("Invalid empty value for parameter \"" + alias + "\".");
        }

        // Find a handler
        Optional<ParameterHandler> optionalHandler = Prism.getParameterHandler(alias);
        if (!optionalHandler.isPresent()) {
            throw new ParameterException("\"" + alias + "\" is not a valid parameter. No handler found.");
        }

        ParameterHandler handler = optionalHandler.get();

        // Allows this command source?
        if (!handler.acceptsSource(session.getCommandSource().get())) {
            throw new ParameterException("This command source may not use the \"" + alias + "\" parameter.");
        }

        // Validate value
        if (!handler.acceptsValue(value)) {
            throw new ParameterException("Invalid value \"" + value + "\" for parameter \"" + alias + "\".");
        }

        return handler.process(session, alias, value, query);
    }
}
