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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandSource;

import com.helion3.prism.Prism;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.results.ResultRecord;

public class AsyncUtils {
    private AsyncUtils() {}

    /**
     * Helper utility for running a lookup asynchronously.
     *
     * @param source CommandSource running this lookup.
     * @param session
     */
    public static void lookup(final QuerySession session) {
        if (!session.getCommandSource().isPresent()) {
            // @todo handle this. would be best with a callback system
        }

        CommandSource source = session.getCommandSource().get();

        async(session, new AsyncCallback() {
            @Override
            public void success(List<ResultRecord> results) {
                source.sendMessage(Format.heading("Showing " + results.size() + " results."));

                for (ResultRecord result : results) {
                    source.sendMessage(Messages.from(result));
                }
            }

            @Override
            public void empty() {
                // @todo move to language files
                source.sendMessage(Format.error(Text.of("Nothing found. See /pr ? for help.")));
            }

            @Override
            public void error(Exception e) {
                // @todo move to language files
                source.sendMessage(Format.error(Text.of("An error occurred. Please see the console.")));
            }
        });
    }

    /**
     * Internal helper for executing database queries asynchronously.
     *
     * @param session QuerySession
     * @param callback AsyncCallback describing the success, empty, and error callbacks.
     */
    private static void async(final QuerySession session, AsyncCallback callback) {
        Prism.getGame().getScheduler().createTaskBuilder().async().execute(new Runnable(){
            @Override
            public void run(){
                try {
                    CompletableFuture<List<ResultRecord>> future = Prism.getStorageAdapter().records().query(session);
                    future.thenAccept(results -> {
                        if (results.isEmpty()) {
                            callback.empty();
                        } else {
                            callback.success(results);
                        }
                    });
                } catch (Exception e) {
                    callback.error(e);
                    e.printStackTrace();
                }
            }
        }).submit(Prism.getPlugin());
    }
}
