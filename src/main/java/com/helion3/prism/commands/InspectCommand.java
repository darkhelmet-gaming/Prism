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

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.command.CommandResult;

import com.helion3.prism.Prism;
import com.helion3.prism.util.Format;

public class InspectCommand {
    private InspectCommand(){}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
        .permission("prism.inspect")
        .executor((source, args) -> {
            if (source instanceof Player) {
                Player player = (Player) source;

                if (Prism.getActiveWands().contains(player.getUniqueId())) {
                    Prism.getActiveWands().remove(player.getUniqueId());
                    source.sendMessage(Format.heading("Inspection wand disabled."));
                } else {
                    Prism.getActiveWands().add(player.getUniqueId());
                    source.sendMessage(Format.heading("Inspection wand enabled."));
                }

                return CommandResult.success();
            } else {
                throw new CommandException(Format.error("You must be a player to use this command."));
            }
        }).build();
    }
}
