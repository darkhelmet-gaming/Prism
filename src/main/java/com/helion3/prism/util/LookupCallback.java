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

package com.helion3.prism.util;

import com.helion3.prism.Prism;
import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.records.Result;
import com.helion3.prism.api.records.ResultAggregate;
import com.helion3.prism.api.records.ResultComplete;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.List;

public class LookupCallback extends AsyncCallback {

    private final QuerySession querySession;

    public LookupCallback(QuerySession querySession) {
        this.querySession = querySession;
    }

    @Override
    public void success(List<Result> results) {
        List<Text> messages = new ArrayList<>();
        for (Result result : results) {
            messages.add(buildResult(result));
        }

        if (messages.isEmpty()) {
            this.empty();
            return;
        }

        PaginationList.Builder paginationBuilder = PaginationList.builder();
        paginationBuilder.padding(Text.of(TextColors.DARK_GRAY, "="));
        paginationBuilder.linesPerPage(15);
        paginationBuilder.contents(messages);
        paginationBuilder.build().sendTo(this.querySession.getCommandSource());
    }

    @Override
    public void empty() {
        this.querySession.getCommandSource().sendMessage(Format.error("Nothing found. See /pr ? for help."));
    }

    @Override
    public void error(Exception ex) {
        this.querySession.getCommandSource().sendMessage(Format.error("An error occurred. Please see the console."));
        Prism.getInstance().getLogger().error("Exception thrown by {}", getClass().getSimpleName(), ex);
    }

    private Text buildResult(Result result) {
        Text.Builder resultMessage = Text.builder();
        resultMessage.append(Text.of(TextColors.DARK_AQUA, result.getSourceName(), " "));
        resultMessage.append(Text.of(TextColors.WHITE, result.getEventVerb(), " "));

        Text.Builder hoverMessage = Text.builder();
        hoverMessage.append(Format.prefix(), Text.NEW_LINE);
        hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Source: ", TextColors.WHITE, result.getSourceName(), Text.NEW_LINE));
        hoverMessage.append(Text.of(TextColors.DARK_GRAY, "PrismEvent: ", TextColors.WHITE, result.getEventName(), Text.NEW_LINE));

        String quantity = result.data.getString(DataQueries.Quantity).orElse(null);
        if (StringUtils.isNotBlank(quantity)) {
            resultMessage.append(Text.of(TextColors.DARK_AQUA, quantity, " "));
            hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Quantity: ", TextColors.WHITE, quantity, Text.NEW_LINE));
        }

        String target = result.data.getString(DataQueries.Target).orElse("Unknown");
        if (StringUtils.isNotBlank(target)) {
            resultMessage.append(Text.of(TextColors.DARK_AQUA, Format.item(target, false), " "));
            hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Target: ", TextColors.WHITE, target, Text.NEW_LINE));
        }

        String id = result.data.getString(DataQueries.Id).orElse(null);
        if (StringUtils.isNotBlank(id)) {
            hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Id: ", TextColors.WHITE, id, Text.NEW_LINE));
        }

        String container = result.data.getString(DataQueries.Container).orElse(null);
        if (StringUtils.isNotBlank(container)) {
            hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Container: ", TextColors.WHITE, container, Text.NEW_LINE));
        }

        if (result instanceof ResultAggregate) {
            int count = result.data.getInt(DataQueries.Count).orElse(0);
            if (count > 0) {
                resultMessage.append(Text.of(TextColors.GREEN, "x", count, " "));
                hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Count: ", TextColors.WHITE, count));
            }
        }

        if (result instanceof ResultComplete) {
            ResultComplete resultComplete = (ResultComplete) result;

            resultMessage.append(Text.of(TextColors.WHITE, resultComplete.getRelativeTime()));
            hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Time: ", TextColors.WHITE, resultComplete.getTime(), Text.NEW_LINE));

            DataView location = (DataView) resultComplete.data.get(DataQueries.Location).orElse(null);
            if (location != null) {
                int x = location.getInt(DataQueries.X).orElse(0);
                int y = location.getInt(DataQueries.Y).orElse(0);
                int z = location.getInt(DataQueries.Z).orElse(0);
                World world = location.get(DataQueries.WorldUuid).flatMap(TypeUtil::uuidFromObject).flatMap(Sponge.getServer()::getWorld).orElse(null);

                hoverMessage.append(Text.of(TextColors.DARK_GRAY, "Location: ", TextColors.WHITE, Format.location(x, y, z, world, false)));
                if (this.querySession.hasFlag(Flag.EXTENDED)) {
                    resultMessage.append(Text.of(Text.NEW_LINE, TextColors.GRAY, " - ", Format.location(x, y, z, world, true)));
                    hoverMessage.append(Text.of(Text.NEW_LINE, Text.NEW_LINE, TextColors.GRAY, "Click location to teleport."));
                }
            }
        }

        resultMessage.onHover(TextActions.showText(hoverMessage.build()));
        return resultMessage.build();
    }
}