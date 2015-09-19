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
package com.helion3.prism.api.parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableList;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.utils.DataQueries;

public class ParameterEventName extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile( "[~|!]?[\\w,-]+" );

    /**
     * Parameter handling the event name field.
     */
    public ParameterEventName() {
        // For backwards-compat, we're still using "a" for action.
        // "e" is likely reserved for entity
        super( ImmutableList.of("a", "event") );
    }

    @Override
    public boolean acceptsValue(String parameter) {
        return pattern.matcher(parameter).matches();
    }

    @Override
    public void process(String value, Query query) {
        final String[] nameArgs = value.split(",");

        List<String> eventNames = new ArrayList<String>();
        for (String eventName : nameArgs) {
            // @todo partial name matching, validation
            eventNames.add(eventName);
        }

        query.addCondition(new Condition(DataQueries.EventName, MatchRule.INCLUDES, eventNames));
    }
}