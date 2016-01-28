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

import java.util.concurrent.CompletableFuture;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.AsyncUtil;
import com.helion3.prism.util.Format;

public class LookupCommand {
    private LookupCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .description(Text.of("Search event records."))
            .permission("prism.lookup")
            .arguments(GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("arguments"))))
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    // Create a new query session
                    final QuerySession session = new QuerySession(source);

                    String arguments = null;
                    if (args.<String>getOne("arguments").isPresent()) {
                        arguments = args.<String>getOne("arguments").get();
                    }

                    source.sendMessage(Format.heading("Querying records..."));

                    try {
                        CompletableFuture<Void> future = session.newQueryFromArguments(arguments);
                        future.thenAccept((v) -> {
                            // Pass off to an async lookup helper
                            try {
                                AsyncUtil.lookup(session);
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
                }
            }).build();
    }
}
