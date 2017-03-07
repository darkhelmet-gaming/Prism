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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.api.text.Text;

import com.helion3.prism.Prism;
import com.helion3.prism.api.flags.FlagHandler;
import com.helion3.prism.api.parameters.ParameterException;
import com.helion3.prism.api.parameters.ParameterHandler;
import com.helion3.prism.util.Format;

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
     * @param session QuerySession
     * @param arguments String Parameter: value string
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromArguments(QuerySession session, @Nullable String arguments) throws ParameterException {
        return fromArguments(session, (arguments != null ? arguments.split(" ") : new String[]{}));
    }

    /**
     * Builds a {@link Query} by parsing an array of arguments.
     *
     * @param session QuerySession
     * @param arguments String[] Parameter:value list
     * @return {@link Query} Database query object
     */
    public static CompletableFuture<Query> fromArguments(QuerySession session, @Nullable String[] arguments) throws ParameterException {
        checkNotNull(session);

        Query query = new Query();
        CompletableFuture<Query> future = new CompletableFuture<>();

        // Track all parameter pairs
        Map<String, String> definedParameters = new HashMap<>();

        if (arguments != null && arguments.length > 0) {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            for (String arg : arguments) {
                Optional<CompletableFuture<?>> listenable;

                if (flagPattern.matcher(arg).matches()) {
                    listenable = parseFlagFromArgument(session, query, arg);
                } else {
                    // Get alias/value pair
                    Pair<String, String> pair = getParameterKeyValue(arg);

                    // Parse for handler
                    listenable = parseParameterFromArgument(session, query, pair);

                    // Add to list of defined
                    definedParameters.put(pair.getKey(), pair.getValue());
                }

                listenable.ifPresent(futures::add);
            }

            if (!futures.isEmpty()) {
                CompletableFuture<Void> combinedFuture = CompletableFuture.<Void>allOf(futures.toArray(new CompletableFuture<?>[futures.size()]));
                combinedFuture.thenAccept((q) -> future.complete(query));
            } else {
                future.complete(query);
            }
        } else {
            future.complete(query);
        }

        if (Prism.getConfig().getNode("defaults", "enabled").getBoolean()) {
            // Require any parameter defaults
            StringBuilder defaultsUsed = new StringBuilder();
            for (ParameterHandler handler : Prism.getParameterHandlers()) {
                boolean aliasFound = false;

                for (String alias : handler.getAliases()) {
                    if (definedParameters.containsKey(alias)) {
                        aliasFound = true;
                        break;
                    }
                }

                if (!aliasFound) {
                    handler.processDefault(session, query).ifPresent(stringStringPair -> defaultsUsed.append(stringStringPair.getKey())
                        .append(":").append(stringStringPair.getValue()).append(" "));
                }
            }

            // @todo should move this
            if (defaultsUsed.length() > 0) {
                session.getCommandSource().sendMessage(Format.subduedHeading(Text.of(String.format("Defaults used: %s", defaultsUsed.toString()))));
            }
        }

        return future;
    }

    /**
     * Parses a flag argument.
     *
     * @param session QuerySession current session.
     * @param query Query query being built.
     * @param flag Flag
     * @return Optional<CompletableFuture<?>>
     */
    private static Optional<CompletableFuture<?>> parseFlagFromArgument(QuerySession session, Query query, String flag) throws ParameterException {
        flag = flag.substring(1);

        // Determine the true alias and value
        String value = null;
        if (flag.contains("=")) {
            // Split the parameter: values
            String[] split = flag.split("=");
            flag = split[0];
            if (split.length == 2 && !split[1].trim().isEmpty()) {
                value = split[1];
            }
        }

        // Find a handler
        Optional<FlagHandler> optionalHandler = Prism.getFlagHandler(flag);
        if (!optionalHandler.isPresent()) {
            throw new ParameterException("\"" + flag + "\" is not a valid flag. No handler found.");
        }

        FlagHandler handler = optionalHandler.get();

        // Allows this command source?
        if (!handler.acceptsSource(session.getCommandSource())) {
            throw new ParameterException("This command source may not use the \"" + flag + "\" flag.");
        }

        // Validate value
        if (value != null && !handler.acceptsValue(value)) {
            throw new ParameterException("Invalid value \"" + value + "\" for parameter \"" + flag + "\".");
        }

        return handler.process(session, flag, value, query);
    }

    /**
     * Returns a key/value pair parsed from a parameter string.
     */
    private static Pair<String, String> getParameterKeyValue(String parameter) {
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

        return Pair.of(alias, value);
    }

    /**
     * Parses a parameter argument.
     *
     * @param session QuerySession current session.
     * @param query Query query being built.
     * @param parameter String argument which should be a parameter
     * @return Optional<CompletableFuture<?>>
     * @throws ParameterException
     */
    private static Optional<CompletableFuture<?>> parseParameterFromArgument(QuerySession session, Query query, Pair<String, String> parameter) throws ParameterException {
        // Simple validation
        if (parameter.getKey().length() <= 0 || parameter.getValue().length() <= 0) {
            throw new ParameterException("Invalid empty value for parameter \"" + parameter.getKey() + "\".");
        }

        // Find a handler
        Optional<ParameterHandler> optionalHandler = Prism.getParameterHandler(parameter.getKey());
        if (!optionalHandler.isPresent()) {
            throw new ParameterException("\"" + parameter.getKey() + "\" is not a valid parameter. No handler found.");
        }

        ParameterHandler handler = optionalHandler.get();

        // Allows this command source?
        if (!handler.acceptsSource(session.getCommandSource())) {
            throw new ParameterException("This command source may not use the \"" + parameter.getKey() + "\" parameter.");
        }

        // Validate value
        if (!handler.acceptsValue(parameter.getValue())) {
            throw new ParameterException("Invalid value \"" + parameter.getValue() + "\" for parameter \"" + parameter.getKey() + "\".");
        }

        return handler.process(session, parameter.getKey(), parameter.getValue(), query);
    }
}
