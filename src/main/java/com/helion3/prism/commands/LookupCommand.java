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

//import static org.spongepowered.api.util.command.args.GenericArguments.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.spongepowered.api.Game;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.helion3.prism.Prism;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.records.ResultRecord;
import com.helion3.prism.api.records.ResultRecordAggregate;
import com.helion3.prism.utils.Template;

public class LookupCommand  {

    private LookupCommand(){}

    public static CommandSpec getCommand(Game game) {
        return CommandSpec.builder()
            .setDescription(Texts.of("View/query Prism records"))
            .setExecutor(new CommandExecutor() {
                @Override
                public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

                    Collection<Object> players = args.getAll("player");
                    Prism.getLogger().debug("arg size: " + players.size());

                    // @todo this will come from parameter parsing...
                    Query query = new Query();
                    QuerySession session = new QuerySession( query );

                    try {

                        // Iterate query results
                        List<ResultRecord> results = Prism.getStorageAdapter().query(session);
                        for (ResultRecord result : results) {
                            // Aggregate data
                            if (result instanceof ResultRecordAggregate) {

                                ResultRecordAggregate aggregate = (ResultRecordAggregate) result;

                                // @todo move to config
                                String template = "{player} {event} {subject}";

                                Map<String,String> tokens = new HashMap<String, String>();
                                tokens.put("player", aggregate.player);
                                tokens.put("event", aggregate.eventName);
                                tokens.put("subject", aggregate.subjectName);

                                src.sendMessage(Texts.of(Template.parseTemplate(template, tokens)));

                            }

                            // Complete data
                            else {

                                // @todo display the full record

                            }
                        }

                    } catch (Exception e) {
                        // @todo handle
                        e.printStackTrace();
                    }

                    return CommandResult.builder().successCount(1).build();
                }
            })
            .build();
    }
}
