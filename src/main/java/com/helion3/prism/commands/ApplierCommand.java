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
package com.helion3.prism.commands;

import com.helion3.prism.Prism;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.records.Actionable;
import com.helion3.prism.api.records.ActionableResult;
import com.helion3.prism.api.records.Result;
import com.helion3.prism.util.Format;
import com.helion3.prism.util.Template;
import com.helion3.prism.util.Translation;
import com.helion3.prism.util.WorldUtil;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ApplierCommand {
    private ApplierCommand() {}

    public enum ApplierMode {
        ROLLBACK, RESTORE
    }

    public static CommandSpec getCommand(ApplierMode mode) {
        return CommandSpec.builder()
        .permission("prism.rollback")
        .arguments(GenericArguments.remainingJoinedStrings(Text.of("parameters")))
        .executor((source, args) -> {
            // Create a new query session
            final QuerySession session = new QuerySession(source);

            try {
                source.sendMessage(Format.heading("Querying records..."));

                CompletableFuture<Void> future = session.newQueryFromArguments(args.<String>getOne("parameters").get());
                future.thenAccept((v) -> {
                    session.getQuery().setAggregate(false);
                    session.getQuery().setLimit(Prism.getConfig().getNode("query", "actionable", "limit").getInt());

                    try {
                        List<ActionableResult> actionResults = new ArrayList<ActionableResult>();
                        // Iterate query results
                        CompletableFuture<List<Result>> futureResults = Prism.getStorageAdapter().records().query(session, false);
                        futureResults.thenAccept(results -> {
                            if (results.isEmpty()) {
                                source.sendMessage(Format.error("No results."));
                            } else {
                                try {
                                    // Iterate record results
                                    for (Result result : results) {
                                        if (result instanceof Actionable) {
                                            Actionable actionable = (Actionable) result;

                                            if (mode.equals(ApplierMode.ROLLBACK)) {
                                                actionResults.add(actionable.rollback());
                                            } else {
                                                actionResults.add(actionable.restore());
                                            }
                                        }
                                    }
                                } catch(Exception e) {
                                    e.printStackTrace();
                                }

                                if (source instanceof Player) {
                                    int changes = 0;

                                    if (session.hasFlag(Flag.CLEAN)) {
                                        changes += WorldUtil.removeIllegalBlocks(((Player) source).getLocation(), session.getRadius());
                                        changes += WorldUtil.removeItemEntitiesAroundLocation(((Player) source).getLocation(), session.getRadius());
                                    }

                                    if (session.hasFlag(Flag.DRAIN)) {
                                        changes += WorldUtil.removeLiquidsAroundLocation(((Player) source).getLocation(), session.getRadius());
                                    }

                                    if (changes > 0) {
                                        source.sendMessage(Format.bonus("Cleaning area..."));
                                    }
                                }

                                int appliedCount = 0;
                                int skippedCount = 0;
                                for (ActionableResult result : actionResults) {
                                    if (result.applied()) {
                                        appliedCount++;
                                    } else {
                                        skippedCount++;
                                    }
                                }

                                Map<String,String> tokens = new HashMap<String, String>();
                                tokens.put("appliedCount", ""+appliedCount);
                                tokens.put("skippedCount", ""+skippedCount);

                                String messageTemplate = null;
                                if (skippedCount > 0) {
                                    messageTemplate = Translation.from("rollback.success.withskipped");
                                } else {
                                    messageTemplate = Translation.from("rollback.success");
                                }

                                source.sendMessage(Format.heading(
                                    Text.of(Template.parseTemplate(messageTemplate, tokens)),
                                    " ", Format.bonus(Translation.from("rollback.success.bonus"))
                                ));

                                if (source instanceof Player) {
                                    Prism.getLastActionResults().put((Player) source, actionResults);
                                }
                            }
                        });
                    } catch (Exception e) {
                        source.sendMessage(Format.error(Text.of(e.getMessage())));
                        e.printStackTrace();
                    }
                });
            } catch(Exception e) {
                source.sendMessage(Format.error(Text.of(e.getMessage())));
                e.printStackTrace();
            }

            return CommandResult.success();
        })
        .build();
    }
}
