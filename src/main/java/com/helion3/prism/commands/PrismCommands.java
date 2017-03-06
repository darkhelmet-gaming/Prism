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
package com.helion3.prism.commands;

import java.util.List;

import com.helion3.prism.api.query.Sort;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.helion3.prism.util.Format;

public class PrismCommands {
    private PrismCommands(){}

    /**
     * Build a complete command hierarchy
     * @return
     */
    public static CommandSpec getCommand() {
        // Build child commands
        ImmutableMap.Builder<List<String>, CommandCallable> builder = ImmutableMap.builder();
        builder.put(ImmutableList.of("i", "wand"), InspectCommand.getCommand());
        builder.put(ImmutableList.of("l", "lookup"), LookupCommand.getCommand());
        builder.put(ImmutableList.of("near"), NearCommand.getCommand());
        // Sort order newest first for rollback, and oldest first for restore.
        builder.put(ImmutableList.of("rb", "rollback"), ApplierCommand.getCommand(Sort.NEWEST_FIRST));
        builder.put(ImmutableList.of("rs", "restore"), ApplierCommand.getCommand(Sort.OLDEST_FIRST));
        builder.put(ImmutableList.of("undo"), UndoCommand.getCommand());
        builder.put(ImmutableList.of("ext"), ExtinguishCommand.getCommand());
        builder.put(ImmutableList.of("?", "help"), HelpCommand.getCommand());

        return CommandSpec.builder()
            .executor((source, args) -> {
                // Check permission here, so the node doesn't apply to all child commands
                if (source.hasPermission("prism.info")) {
                    source.sendMessage(Text.of(
                            Format.heading(TextColors.GRAY, "By ", TextColors.GOLD, "viveleroi.\n"),
                            TextColors.DARK_AQUA, "Tracking so good the NSA stole our name.\n",
                            TextColors.GRAY, "Help: ", TextColors.WHITE, "/pr ?\n",
                            TextColors.GRAY, "IRC: ", TextColors.WHITE, "irc.esper.net #prism\n",
                            TextColors.GRAY, "Site: ", TextColors.WHITE, "http://discover-prism.com"
                    ));

                    return CommandResult.success();
                } else {
                    throw new CommandException(Format.error("You do not have permission to use this command."));
                }
            })
            .children(builder.build()).build();
    }
}
