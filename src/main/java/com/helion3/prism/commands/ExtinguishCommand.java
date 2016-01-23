package com.helion3.prism.commands;

import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.helion3.prism.util.Format;
import com.helion3.prism.util.WorldUtil;

public class ExtinguishCommand {
    private ExtinguishCommand() {}

    public static CommandSpec getCommand() {
        return CommandSpec.builder()
            .permission("prism.extinguish")
            .arguments(GenericArguments.integer(Text.of("radius")))
            .executor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource source, CommandContext args) throws CommandException {
                    if (!(source instanceof Player)) {
                        source.sendMessage(Format.error("You must be a player to use this command."));
                        return CommandResult.empty();
                    }

                    int radius = args.<Integer>getOne("radius").get();
                    int changes = WorldUtil.removeAroundFromLocation(BlockTypes.FIRE, ((Player) source).getLocation(), radius);

                    source.sendMessage(Format.message(String.format("Removed %d matches within %d blocks", changes, radius)));

                    return CommandResult.success();
                }
            })
            .build();
    }
}