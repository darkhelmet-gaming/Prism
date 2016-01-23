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

import java.util.Date;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DateUtil;

public class ParameterTime extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    /**
     * Handler for time-related parameters.
     */
    public ParameterTime() {
        super(ImmutableList.of("before", "since"));
    }

    @Override
    public boolean acceptsSource(@Nullable CommandSource source) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        boolean matches = pattern.matcher(value).matches();

        if (matches) {
            try {
                DateUtil.parseTimeStringToDate(value, false);
            } catch(Exception e) {
                matches = false;
            }
        }

        return matches;
    }

    @Override
    public Optional<ListenableFuture<?>> process(QuerySession session, String parameter, String value, Query query) {
        Date date = DateUtil.parseTimeStringToDate(value, false);

        // Determine match rule based on before/since
        MatchRule rule = MatchRule.LESS_THAN_EQUAL;
        if (parameter.equalsIgnoreCase("s") || parameter.equalsIgnoreCase("since")) {
            rule = MatchRule.GREATER_THAN_EQUAL;
        }

        query.addCondition(Condition.of(DataQueries.Created.toString(), rule, date));

        return Optional.empty();
    }
}
