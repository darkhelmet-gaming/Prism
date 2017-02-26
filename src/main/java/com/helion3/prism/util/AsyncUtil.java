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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.records.Result;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationService;

import com.helion3.prism.Prism;
import com.helion3.prism.api.query.QuerySession;

public class AsyncUtil {
    private AsyncUtil() {}

    /**
     * Helper utility for running a lookup asynchronously.
     *
     * @param session The {@link QuerySession} running this lookup
     */
    public static void lookup(final QuerySession session) {
        if (!session.getCommandSource().isPresent()) {
            // @todo handle this.
            return;
        }

        CommandSource source = session.getCommandSource().get();

        // Enforce lookup limits
        session.getQuery().setLimit(Prism.getConfig().getNode("query", "lookup", "limit").getInt());

        async(session, new AsyncCallback() {
            @Override
            public void success(List<Result> results) {
                if (results.size() > 5) {
                    List<Text> messages = results.stream()
                       .map(result -> Messages.from(result, session.hasFlag(Flag.EXTENDED))).collect(Collectors.toList());

                    Optional<PaginationService> service = Prism.getGame().getServiceManager().provide(PaginationService.class);
                    if (service.isPresent()) {
                        // Build paginated content
                        PaginationList.Builder builder = service.get().builder();
                        builder.contents(messages);
                        builder.sendTo(session.getCommandSource().get());
                        builder.linesPerPage(5);
                    }
                } else {
                    for (Result result : results) {
                        session.getCommandSource().ifPresent(p -> p.sendMessage(Messages.from(result, session.hasFlag(Flag.EXTENDED))));
                    }
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
     * @param session The {@link QuerySession} to query with
     * @param callback An {@link AsyncCallback} describing the success, empty, and error callbacks
     */
    private static void async(final QuerySession session, AsyncCallback callback) {
        Prism.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
            try {
                CompletableFuture<List<Result>> future = Prism.getStorageAdapter().records().query(session, true);
                future.thenAccept(results -> {
                    try {
                        if (results.isEmpty()) {
                            callback.empty();
                        } else {
                            callback.success(results);
                        }
                    } catch(Exception e) {
                        session.getCommandSource().ifPresent(p -> p.sendMessage(Format.error(e.getMessage())));
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                callback.error(e);
                e.printStackTrace();
            }
        }).submit(Prism.getPlugin());
    }
}
