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

import com.helion3.prism.Prism;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.records.Result;
import org.spongepowered.api.Sponge;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class AsyncUtil {
    private AsyncUtil() {}

    /**
     * Helper utility for running a lookup asynchronously.
     *
     * @param session QuerySession running this lookup.
     */
    public static void lookup(final QuerySession session) {
        // Enforce lookup limits
        session.getQuery().setLimit(Prism.getInstance().getConfig().getLimitCategory().getMaximumLookup());
        async(session, new LookupCallback(session));
    }

    /**
     * Internal helper for executing database queries asynchronously.
     *
     * @param session QuerySession
     * @param callback AsyncCallback describing the success, empty, and error callbacks.
     */
    private static void async(final QuerySession session, AsyncCallback callback) {
        Sponge.getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                CompletableFuture<List<Result>> future = Prism.getInstance().getStorageAdapter().records().query(session, true);
                future.thenAccept(results -> {
                    try {
                        if (results.isEmpty()) {
                            callback.empty();
                        } else {
                            callback.success(results);
                        }
                    } catch(Exception e) {
                        session.getCommandSource().sendMessage(Format.error(e.getMessage()));
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                callback.error(e);
                e.printStackTrace();
            }
        }).submit(Prism.getInstance().getPluginContainer());
    }
}
