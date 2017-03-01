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
package com.helion3.prism.api.records;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import com.helion3.prism.Prism;
import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.DateUtil;

/**
 * Represents a complete copy of event record data from
 * a query result. Used for displaying individual entries
 * or for non-lookup actions.
 *
 */
public class ResultComplete extends Result {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat(Prism.getConfig().getNode("display", "dateFormat").getString());

    /**
     * Returns a user-friendly relative "time since" value.
     *
     * @return String "time since" value.
     */
    public String getRelativeTime() {
        Optional<Object> date = data.get(DataQueries.Created);
        String relativeTime = "";

        if (date.isPresent()) {
            Date created = null;

            if (date.get() instanceof Date) {
                created = (Date) date.get();
            }
            else if (date.get() instanceof Long) {
                created = new Date(((Long) date.get()) * 1000);
            }

            if (created != null) {
                relativeTime = DateUtil.getTimeSince(created);
            }
        }

        return relativeTime;
    }

    /**
     * Returns a full timestamp.
     *
     * @return String timestamp
     */
    public String getTime() {
        Optional<Object> date = data.get(DataQueries.Created);
        String time = "";

        if (date.isPresent()) {
            if (date.get() instanceof Date) {
                time = dateFormatter.format((Date) date.get());
            }
            else if (date.get() instanceof Long) {
                time = dateFormatter.format(new Date(((Long) date.get()) * 1000));
            }
        }

        return time;
    }
}