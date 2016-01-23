package com.helion3.prism.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.format.TextColors;

import com.helion3.prism.util.Format;

public class HelpCommand {
    private HelpCommand(){}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    source.sendMessage(Format.message("/pr [l|lookup] (params)", TextColors.GRAY, " - Query the database."));
                    source.sendMessage(Format.message("/pr [rb|rollback] (params)", TextColors.GRAY, " - Reverse changes, limited by parameters."));
                    source.sendMessage(Format.message("/pr [rs|restore] (params)", TextColors.GRAY, " - Re-apply changes, limited by parameters."));
                    source.sendMessage(Format.message("/pr undo", TextColors.GRAY, " - Reverse your last rollback/restore."));
                    source.sendMessage(Format.message("/pr i", TextColors.GRAY, " - Toggle the inspection wand."));
                    return CommandResult.empty();
                }
            }).build();
    }
}