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

import com.google.common.base.Optional;
import com.helion3.prism.Prism;
import com.helion3.prism.api.parameters.ParameterHandler;

final public class Query {

    private boolean isAggregate = true;
    private List<String> eventNames = new ArrayList<String>();
    private MatchRule eventMatchRule = MatchRule.INCLUDE;

    /**
     * Builds a {@link Query} by parsing a string of parameters
     * and their values.
     *
     * @param parameters String Parameter: value string
     * @return {@link Query} Database query object
     */
    public static Query fromParameters(String parameters) {
        checkNotNull(parameters);
        return fromParameters(parameters.split(" "));
    }

    /**
     * Builds a {@link Query} by parsing an array of parameters
     * and their values.
     *
     * @param parameters String[] Parameter:value list
     * @return {@link Query} Database query object
     */
    public static Query fromParameters(String[] parameters) {
        Query query = new Query();

        if (parameters.length > 0) {
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
                    // Any value with a defined parameter is assumed to be a
                    // player username.
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

                // Validate value
                if (!handler.acceptsValue(value)) {
                    // @todo throw syntax error
                    break;
                }

                handler.process(value, query);
            }
        }

        return query;
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
     * Returns the {@link MatchRule} the event condition will use.
     * @return MatchRule
     */
    public MatchRule getEventNameMatchRule() {
        return eventMatchRule;
    }

    /**
     * Sets the {@link MatchRule} the event condition will use.
     * @param rule {@link MatchRule}
     */
    public void setEventNameMatchRule(MatchRule rule) {
        eventMatchRule = rule;
    }

    /**
     * Add a named event to the query condition. This will be
     * a white or black list depending on the event {@link MatchRule}
     * @param action String action name
     */
    public void addEventName(String action) {
        checkNotNull(action);
        eventNames.add(action);
    }

    /**
     * Returns a list of named actions.
     * @return List of named actions.
     */
    public List<String> getEventNames() {
        return eventNames;
    }
}