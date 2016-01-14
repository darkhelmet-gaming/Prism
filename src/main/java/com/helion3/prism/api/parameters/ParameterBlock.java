package com.helion3.prism.api.parameters;

import java.util.Optional;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.utils.DataQueries;

public class ParameterBlock extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    /**
     * Parameter handling a radius around a single location.
     */
    public ParameterBlock() {
        super(ImmutableList.of("b", "block"));
    }

    @Override
    public boolean acceptsSource(@Nullable CommandSource source) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        return pattern.matcher(value).matches();
    }

    @Override
    public Optional<ListenableFuture<?>> process(QuerySession session, String parameter, String value, Query query) {
        query.addCondition(Condition.of(DataQueries.OriginalBlock + "." + DataQueries.BlockState + "." + DataQueries.BlockType, MatchRule.EQUALS, Pattern.compile(value)));

        return Optional.empty();
    }
}