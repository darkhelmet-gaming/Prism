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
package com.helion3.prism.util;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Text.Builder;
import org.spongepowered.api.text.format.TextColors;

import com.helion3.prism.api.results.ResultRecord;
import com.helion3.prism.api.results.ResultRecordAggregate;

public class Messages {
    private Messages() {}

    /**
     * Generates Text output from a ResultRecord.
     *
     * @param result ResultRecord
     * @return Text
     */
    public static Text from(ResultRecord result) {
        Builder builder = Text.builder().append(Text.of(
            TextColors.DARK_AQUA, result.getSourceName(), " ",
            TextColors.WHITE, result.getEventVerb(), " "
        ));

        String target = result.getTargetName();
        if (!target.isEmpty()) {
            builder.append(Text.of(TextColors.DARK_AQUA, target, " "));
        }

        if (result instanceof ResultRecordAggregate) {
            int count = result.data.getInt(DataQueries.Count).get();
            builder.append(Text.of(TextColors.GREEN, "x" + count, " "));
        }

        builder.append(Text.of(TextColors.WHITE, result.getRelativeTime()));

        return builder.build();
    }
}
