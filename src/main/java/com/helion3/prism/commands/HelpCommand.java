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

import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.format.TextColors;

import com.helion3.prism.util.Format;

public class HelpCommand {
    private HelpCommand(){}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .permission("prism.help")
            .executor((source, args) -> {
                source.sendMessage(Format.message("/pr [l|lookup] (params)", TextColors.GRAY, " - Query the database."));
                source.sendMessage(Format.message("/pr near", TextColors.GRAY, " - Quick lookup of nearby activity."));
                source.sendMessage(Format.message("/pr [rb|rollback] (params)", TextColors.GRAY, " - Reverse changes, limited by parameters."));
                source.sendMessage(Format.message("/pr [rs|restore] (params)", TextColors.GRAY, " - Re-apply changes, limited by parameters."));
                source.sendMessage(Format.message("/pr undo", TextColors.GRAY, " - Reverse your last rollback/restore."));
                source.sendMessage(Format.message("/pr i", TextColors.GRAY, " - Toggle the inspection wand."));
                return CommandResult.success();
            }).build();
    }
}