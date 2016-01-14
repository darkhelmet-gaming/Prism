package com.helion3.prism.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.command.CommandCallable;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import com.helion3.prism.Prism;
import com.helion3.prism.api.results.ActionableResult;
import com.helion3.prism.utils.Format;
import com.helion3.prism.utils.Template;
import com.helion3.prism.utils.Translation;

public class UndoCommand implements CommandCallable {
    @Override
    public CommandResult process(CommandSource source, String arguments) throws CommandException {
        if (!(source instanceof Player)) {
            source.sendMessage(Format.error("You must be a player to use this command."));
            return CommandResult.empty();
        }

        List<ActionableResult> results = Prism.getLastActionResults().get(source);
        if (results == null) {
            source.sendMessage(Format.error("You have no valid actions to undo."));
            return CommandResult.empty();
        }

        int applied = 0;
        int skipped = 0;

        for (ActionableResult result : results) {
            if (result.getTransaction().isPresent()) {
                Object rawOriginal = result.getTransaction().get().getOriginal();

                if (rawOriginal instanceof BlockSnapshot) {
                    if (((BlockSnapshot)rawOriginal).restore(true, true)) {
                        applied++;
                    } else {
                        skipped++;
                    }
                }
            }
        }

        Map<String,String> tokens = new HashMap<String, String>();
        tokens.put("appliedCount", "" + applied);
        tokens.put("skippedCount", "" + skipped);

        String messageTemplate = null;
        if (skipped > 0) {
            messageTemplate = Translation.from("rollback.success.withskipped");
        } else {
            messageTemplate = Translation.from("rollback.success");
        }

        source.sendMessage(Format.heading(
            Text.of(Template.parseTemplate(messageTemplate, tokens)),
            " ", Format.bonus(Translation.from("rollback.success.bonus"))
        ));

        return CommandResult.success();
    }

    @Override
    public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
        return null;
    }

    @Override
    public boolean testPermission(CommandSource source) {
        return source.hasPermission("prism.rollback");
    }

    @Override
    public Optional<Text> getShortDescription(CommandSource source) {
        return null;
    }

    @Override
    public Optional<Text> getHelp(CommandSource source) {
        return null;
    }

    @Override
    public Text getUsage(CommandSource source) {
        return null;
    }
}
