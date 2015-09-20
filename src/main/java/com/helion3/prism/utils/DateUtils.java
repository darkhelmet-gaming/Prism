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
package com.helion3.prism.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtils {
    private DateUtils() {}

    private static final Pattern relativeTimeDeclaration = Pattern.compile("([0-9]+)(s|h|m|d|w)");

    /**
     * Returns a user-friendly relative "time since" value for two dates.
     *
     * @param start Starting time. Must be prior to present.
     * @return String relative "time since" value.
     */
    public static String getTimeSince(final Date start) {
        String time_ago = "";

        long diffInSeconds = ( new Date().getTime() - start.getTime() ) / 1000;
        long diff[] = new long[] { 0, 0, 0, 0 };

        diff[3] = ( diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds );
        diff[2] = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 60 ? diffInSeconds % 60 : diffInSeconds;
        diff[1] = ( diffInSeconds = ( diffInSeconds / 60 ) ) >= 24 ? diffInSeconds % 24 : diffInSeconds;
        diff[0] = ( diffInSeconds = ( diffInSeconds / 24 ) );

        // Only show days if more than 1
        if( diff[0] >= 1 ) {
            time_ago += diff[0] + "d";
        }
        // Only show hours if > 1
        if( diff[1] >= 1 ) {
            time_ago += diff[1] + "h";
        }
        // Only show minutes if > 1 and less than 60
        if( diff[2] > 1 && diff[2] < 60 ) {
            time_ago += diff[2] + "m";
        }
        if( !time_ago.isEmpty() ) {
            time_ago += " ago";
        }

        if( diff[0] == 0 && diff[1] == 0 && diff[2] <= 1 ) {
            time_ago = "just now";
        }

        return time_ago;
    }

    public static Long translateTimeStringToDate(String arg_value) {
        Long dateFrom = 0L;

        final Calendar calendar = Calendar.getInstance();

        final String[] matches = TypeUtils.preg_match_all(relativeTimeDeclaration, arg_value);
        if (matches.length > 0) {
            for (final String match : matches) {
                final Matcher m = relativeTimeDeclaration.matcher(match);
                if (m.matches()) {
                    if (m.groupCount() == 2) {
                        final int tfValue = Integer.parseInt(m.group(1));
                        final String tfFormat = m.group(2);

                        if (tfFormat.equals("w")) {
                            calendar.add(Calendar.WEEK_OF_YEAR, -1 * tfValue);
                        }
                        else if( tfFormat.equals( "d" ) ) {
                            calendar.add(Calendar.DAY_OF_MONTH, -1 * tfValue);
                        }
                        else if (tfFormat.equals( "h" ) ) {
                            calendar.add(Calendar.HOUR, -1 * tfValue);
                        }
                        else if (tfFormat.equals( "m" )) {
                            calendar.add(Calendar.MINUTE, -1 * tfValue);
                        }
                        else if (tfFormat.equals("s")) {
                            calendar.add(Calendar.SECOND, -1 * tfValue);
                        } else {
                            return null;
                        }
                    }
                }
            }
            dateFrom = calendar.getTime().getTime();
        }

        return dateFrom;

    }
}
