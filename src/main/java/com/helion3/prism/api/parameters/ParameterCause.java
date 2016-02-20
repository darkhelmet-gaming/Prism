package com.helion3.prism.api.parameters;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.util.DataQueries;
import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.regex.Pattern;

public class ParameterCause extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    /**
     * Parameter handling non-player causes.
     */
    public ParameterCause() {
        super(ImmutableList.of("c", "cause"));
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
        query.addCondition(FieldCondition.of(DataQueries.Cause, MatchRule.EQUALS, Pattern.compile(value)));

        return Optional.empty();
    }
}
