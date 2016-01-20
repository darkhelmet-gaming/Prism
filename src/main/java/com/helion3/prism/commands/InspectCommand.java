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

import java.util.List;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;

import com.helion3.prism.Prism;
import com.helion3.prism.utils.Format;

public class InspectCommand implements CommandCallable {
    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (source instanceof Player) {
            Player player = (Player) source;

            if (Prism.getActiveWands().contains(player)) {
                Prism.getActiveWands().remove(player);
                source.sendMessage(Format.heading("Inspection wand disabled."));
            } else {
                Prism.getActiveWands().add(player);
                source.sendMessage(Format.heading("Inspection wand enabled."));
            }
        } else {
            // @todo handle...
        }

        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("prism.lookup");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return Optional.of(Text.of("Toggle inspection wand."));
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return Optional.of(Text.of("Left or right-click a location to inspect it."));
    }

    @Override
    public Text getUsage(CommandSource source) {
        return Text.of("/pr i");
    }
}
