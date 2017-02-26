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
package com.helion3.prism.api.flags;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;

import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

public interface FlagHandler {
    /**
     * Returns whether this flag is allowed for the current command source.
     *
     * @param source The command source of the current flag
     * @return Whether or not this command source may use this flag
     */
    boolean acceptsSource(@Nullable CommandSource source);

    /**
     * Returns whether the given value(s) for the handler are acceptable.
     *
     * @param value The string value/input for the parameter
     * @return Whether or not this value is legal for this parameter.
     */
    boolean acceptsValue(String value);

    /**
     * Returns whether this handler responds to the given flag.
     *
     * @param flag The flag to check against as a string
     * @return Whether or not this handler responds to an flag
     */
    boolean handles(String flag);

    /**
     * Processes the given value into conditions which are then
     * appended to the query.
     *
     * @param session The current {@link QuerySession}
     * @param flag The flag used as a string
     * @param value An string value(s) given with flag
     * @param query The current {@link Query} object
     * @return A {@link CompletableFuture} of the process, if available
     */
    Optional<CompletableFuture<?>> process(QuerySession session, String flag, String value, Query query);
}
