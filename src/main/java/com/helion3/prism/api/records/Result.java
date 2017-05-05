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

import com.helion3.prism.Prism;
import org.spongepowered.api.data.DataContainer;

import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.TypeUtil;

abstract public class Result {
    public DataContainer data;

    /**
     * Returns a verb variant of the event group name.
     *
     * @return String verb variant of event group.
     */
    public String getEventVerb() {
        return TypeUtil.translateToPastTense(getEventName());
    }

    /**
     * Returns the event name.
     *
     * @return String event name.
     */
    public String getEventName() {
        return data.getString(DataQueries.EventName).orElse("unknown");
    }

    /**
     * Returns a user-friendly string describing the source.
     *
     * @return String source name.
     */
    public String getSourceName() {
        return data.getString(DataQueries.Cause).orElse("unknown");
    }

    /**
     * Returns a user-friendly name of the target item,
     * block, or entity of this event record.
     *
     * @return String target name.
     */
    public String getTargetName() {
        return formatId(data.getString(DataQueries.Target).orElse(""));
    }

    /**
     * Returns the quantity as a string of the target item,
     * block, or entity of this event record.
     *
     * @return String quantity.
     */
    public String getTargetQuantity() {
        return formatId(data.getString(DataQueries.Quantity).orElse(""));
    }

    /**
     * Strips ID prefixes, like "minecraft:".
     *
     * @param id String ID
     * @return String
     */
    private String formatId(String id) {
        if (id.contains(":")) {
            id = id.split(":")[1];
        }

        return id;
    }

    /**
     * Instantiate an appropriate Result for the event.
     * @param eventName String name of event
     * @param isAggregate boolean if aggregate
     * @return Result
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static Result from(String eventName, boolean isAggregate) throws IllegalAccessException, InstantiationException {
        // Build our result object
        final Result result;
        if (isAggregate) {
            result = new ResultAggregate();
        } else {
            // Pull record class for this event, if any
            Class<? extends Result> clazz = Prism.getResultRecord(eventName);
            if (clazz != null){
                result = clazz.newInstance();
            } else {
                result = new ResultComplete();
            }
        }

        return result;
    }
}