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
package com.helion3.prism.util;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtil {
    private DateUtil() {}

    private static final Pattern relativeTimeDeclaration = Pattern.compile("([0-9]+)(s|h|m|d|w)");

    /**
     * Returns a user-friendly relative "time since" value for two dates.
     *
     * @param start Starting time. Must be prior to present.
     * @return String relative "time since" value.
     */
    public static String getTimeSince(final Date start) {
        String result = "";

        long diffInSeconds = ( new Date().getTime() - start.getTime() ) / 1000;
        long diff[] = new long[] { 0, 0, 0, 0 };

        diff[3] = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        diff[2] = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = ( diffInSeconds / 24 );

        // Only show days if more than 1
        if( diff[0] >= 1 ) {
            result += diff[0] + "d";
        }
        // Only show hours if > 1
        if( diff[1] >= 1 ) {
            result += diff[1] + "h";
        }
        // Only show minutes if > 1 and less than 60
        if( diff[2] > 1 && diff[2] < 60 ) {
            result += diff[2] + "m";
        }
        if( !result.isEmpty() ) {
            result += " ago";
        }

        if( diff[0] == 0 && diff[1] == 0 && diff[2] <= 1 ) {
            result = "just now";
        }

        return result;
    }

    /**
     * Parses a special time/date shorthand into a Date.
     *
     * @param shorthand String shorthand value.
     * @return Date final object.
     */
    public static Date parseTimeStringToDate(String shorthand, boolean future) {
        final Calendar calendar = Calendar.getInstance();

        final String[] matches = TypeUtil.pregMatchAll(relativeTimeDeclaration, shorthand);
        if (matches.length == 0) {
            throw new IllegalArgumentException("Invalid date shorthand.");
        }

        for (final String match : matches) {
            final Matcher m = relativeTimeDeclaration.matcher(match);
            if (m.matches()) {
                if (m.groupCount() == 2) {
                    final int tfValue = Integer.parseInt(m.group(1));
                    final String tfFormat = m.group(2);

                    switch (tfFormat) {
                        case "w":
                            calendar.add(Calendar.WEEK_OF_YEAR, (future ? 1 : -1) * tfValue);
                            break;
                        case "d":
                            calendar.add(Calendar.DAY_OF_MONTH, (future ? 1 : -1) * tfValue);
                            break;
                        case "h":
                            calendar.add(Calendar.HOUR, (future ? 1 : -1) * tfValue);
                            break;
                        case "m":
                            calendar.add(Calendar.MINUTE, (future ? 1 : -1) * tfValue);
                            break;
                        case "s":
                            calendar.add(Calendar.SECOND, (future ? 1 : -1) * tfValue);
                            break;
                        default:
                            return null;
                    }
                }
            }
        }

        return calendar.getTime();
    }
}
