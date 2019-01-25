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

import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TypeUtil {
    private TypeUtil() {}

    /**
     * Java implementation of preg_match_all by https://github.com/raimonbosch.
     *
     * @param p Pattern
     * @param subject String value to match against.
     * @return Array of matches.
     */
    public static String[] pregMatchAll(Pattern p, String subject) {
        Matcher m = p.matcher(subject);
        StringBuilder out = new StringBuilder();
        boolean split = false;
        while (m.find()) {
            out.append(m.group());
            out.append("~");
            split = true;
        }
        return (split) ? out.toString().split("~") : new String[0];
    }

    /**
     * Converts UUID to a string ready for use against a MySQL database.
     *
     * @param id UUID
     */
    public static String uuidToDbString(UUID id) {
        return uuidStringToDbString(id.toString());
    }

    /**
     * Converts UUID string to a string ready for use against a MySQL database.
     *
     * @param id String
     */
    public static String uuidStringToDbString(String id) {
        return id.replace("-", "");
    }

    /**
     * Converts an unhyphenated UUID string to a UUID.
     *
     * @param uuid String
     */
    public static UUID uuidFromDbString(String uuid) {
        return UUID.fromString(uuidStringFromDbString(uuid));
    }

    /**
     * Converts an unhyphenated UUID string to a UUID String.
     *
     * @param uuid String
     */
    public static String uuidStringFromDbString(String uuid) {
        // Positions need to be -2
        String completeUuid = uuid.substring(0, 8);
        completeUuid += "-" + uuid.substring(8,12);
        completeUuid += "-" + uuid.substring(12,16);
        completeUuid += "-" + uuid.substring(16,20);
        completeUuid += "-" + uuid.substring(20);
        return completeUuid.toLowerCase();
    }

    /**
     * Converts an object to a UUID.
     *
     * @param uniqueId Object
     */
    public static Optional<UUID> uuidFromObject(Object uniqueId) {
        try {
            if (uniqueId instanceof String) {
                return Optional.of(UUID.fromString(uniqueId.toString()));
            } else if (uniqueId instanceof UUID) {
                return Optional.of((UUID) uniqueId);
            }

            return Optional.empty();
        } catch (IllegalArgumentException ex) {
            return Optional.empty();
        }
    }
}
