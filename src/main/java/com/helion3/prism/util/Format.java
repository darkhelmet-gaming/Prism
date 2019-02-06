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

import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.net.MalformedURLException;
import java.net.URL;

public class Format {

    private Format() {
    }

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
        return Text.of(prefix(), TextColors.RED, content);
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
        return Text.of(prefix(), TextColors.WHITE, content);
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
        return Text.of(prefix(), TextColors.GRAY, content);
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
        return Text.of(prefix(), TextColors.GREEN, content);
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

    /**
     * Returns content formatted with the Plugin name.
     * @return Text Formatted content.
     */
    public static Text prefix() {
        return Text.of(TextColors.LIGHT_PURPLE, Reference.NAME, " //", TextColors.RESET, " ");
    }

    /**
     * Returns content formatted with a URL.
     *
     * @param url URL
     * @return Text Formatted content.
     */
    public static Text url(String url) {
        Text.Builder textBuilder = Text.builder();
        textBuilder.append(Text.of(TextColors.BLUE, url));

        try {
            textBuilder.onClick(TextActions.openUrl(new URL(url)));
        } catch (MalformedURLException ex) {
            textBuilder.onClick(TextActions.suggestCommand(url));
        }

        return textBuilder.build();
    }

    /**
     * Returns content formatted with an Item name.
     * Optionally a hover action can be added to display
     * the full Item id.
     * @param id Item Id
     * @param hoverAction Hover Action
     * @return Text Formatted content.
     */
    public static Text item(String id, boolean hoverAction) {
        checkNotNull(id);

        Text.Builder textBuilder = Text.builder();
        if (StringUtils.contains(id, ":")) {
            textBuilder.append(Text.of(StringUtils.substringAfter(id, ":")));
        } else {
            textBuilder.append(Text.of(id));
        }

        if (hoverAction) {
            textBuilder.onHover(TextActions.showText(Text.of(id)));
        }

        return textBuilder.build();
    }

    /**
     * Return content formatted with location information.
     * Optionally a click action can be added to teleport
     * the message recipients to the provided location.
     * @param x X Coordinate
     * @param y Y Coordinate
     * @param z Z Coordinate
     * @param world World
     * @param clickAction Click Action
     * @return Text Formatted content.
     */
    public static Text location(int x, int y, int z, World world, boolean clickAction) {
        Text.Builder textBuilder = Text.builder();
        textBuilder.append(Text.of("(x:", x, " y:", y, " z:", z));
        if (world != null) {
            textBuilder.append(Text.of(" world:", world.getName()));

            if (clickAction) {
                textBuilder.onClick(TextActions.executeCallback(commandSource -> {
                    if (!(commandSource instanceof Player)) {
                        return;
                    }

                    ((Player) commandSource).setLocation(world.getLocation(x, y, z));
                }));
            }
        }

        return textBuilder.append(Text.of(")")).build();
    }
}