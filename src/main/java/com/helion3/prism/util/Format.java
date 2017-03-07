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

import static com.google.common.base.Preconditions.*;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

public class Format {

    private static final String namespace = "Prism";

    private Format() {}

    /**
     * Returns content formatted as an error message
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text error(Object...objects) {
        return error(Text.of(objects));
    }

    /**
     * Returns content formatted as an error message
     * @param content Text Content to format
     * @return Text Formatted content.
     */
    public static Text error(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.LIGHT_PURPLE, namespace, " // ", TextColors.RED, content);
    }

    /**
     * Returns content formatted as a "heading"
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text heading(Object...objects) {
        return heading(Text.of(objects));
    }

    /**
     * Returns content formatted as a "heading"
     * @param content Text Content to format
     * @return Text Formatted content.
     */
    public static Text heading(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.LIGHT_PURPLE, namespace, " // ", TextColors.WHITE, content);
    }

    /**
     * Returns content formatted as a standard message
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text message(Object...objects) {
        return message(Text.of(objects));
    }

    /**
     * Returns content formatted as a standard message
     * @param content Text Content to format
     * @return Text Formatted content.
     */
    public static Text message(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.WHITE, content);
    }

    /**
     * Returns content formatted as a "subdued heading"
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text subduedHeading(Object...objects) {
        return subduedHeading(Text.of(objects));
    }

    /**
     * Returns content formatted as a "subdued heading"
     * @param content Text Content to format
     * @return Text Formatted content.
     */
    public static Text subduedHeading(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.LIGHT_PURPLE, namespace, " // ", TextColors.GRAY, content);
    }

    /**
     * Returns content formatted as a success message
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text success(Object...objects) {
        return success(Text.of(objects));
    }

    /**
     * Returns content formatted as a success message
     * @param content Text Content to format
     * @return Text Formatted content.
     */
    public static Text success(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.LIGHT_PURPLE, namespace, " // ", TextColors.GREEN, content);
    }

    /**
     * Returns content formatted as a bonus message
     * @param objects Object[] Content to format
     * @return Text Formatted content.
     */
    public static Text bonus(Object...objects) {
        return bonus(Text.of(objects));
    }

    /**
     * Returns content formatted as a bonus string. Usually used
     * for fun wording inside other messages.
     * @param content  Text Content to format
     * @return Text Formatted content.
     */
    public static Text bonus(Text content) {
        checkNotNull(content);
        return Text.of(TextColors.GRAY, content);
    }
}