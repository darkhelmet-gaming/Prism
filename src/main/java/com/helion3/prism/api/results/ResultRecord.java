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
package com.helion3.prism.api.results;

import java.util.Optional;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;

import com.helion3.prism.util.DataQueries;
import com.helion3.prism.util.TypeUtil;

abstract public class ResultRecord {
    public DataContainer data;

    /**
     * Returns a verb variant of the event group name.
     *
     * @return String verb variant of event group.
     */
    public String getEventVerb() {
        return TypeUtil.translateToPastTense(getEventGroupName());
    }

    /**
     * Returns the group portion of an event name.
     *
     * @return String event group name.
     */
    public String getEventGroupName() {
        String value = getEventName();

        if (value.contains("-")) {
            value = value.split("-")[1];
        }

        return value;
    }

    /**
     * Returns the event name.
     *
     * @return String event name.
     */
    public String getEventName() {
        String value = "unknown";

        Optional<String> optional = data.getString(DataQueries.EventName);
        if (optional.isPresent()) {
            value = optional.get();
        }

        return value;
    }

    /**
     * Returns a user-friendly string describing the source.
     *
     * @return String source name.
     */
    public String getSourceName() {
        String value = "unknown";

        Optional<String> optional = data.getString(DataQueries.Cause);
        if (optional.isPresent()) {
            value = optional.get();
        }

        return value;
    }

    /**
     * Returns a user-friendly name of the target item,
     * block, or entity of this event record.
     *
     * @return String target name.
     */
    public String getTargetName() {
        String value = "";
        String eventName = data.getString(DataQueries.EventName).get();

        // Determine which block state we're using
        DataQuery path = DataQueries.OriginalBlock.then(DataQueries.BlockState).then(DataQueries.BlockType);
        if (eventName.equals("place")) {
            path = DataQueries.ReplacementBlock.then(DataQueries.BlockState).then(DataQueries.BlockType);
        }

        // Use value
        Optional<String> optionalBlockType = data.getString(path);
        if (optionalBlockType.isPresent()) {
            value = optionalBlockType.get();

            if (value.contains(":")) {
                value = value.split(":")[1];
            }
        }

        return value;
    }

    /**
     * Returns a user-friendly relative "time since" value.
     *
     * @return String "time since" value.
     */
    public String getRelativeTime() {
        // todo varies based on aggregate or complete
        return "";
    }
}