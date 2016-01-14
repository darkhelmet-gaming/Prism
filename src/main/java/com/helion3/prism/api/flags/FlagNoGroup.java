package com.helion3.prism.api.flags;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

public class FlagNoGroup extends SimpleFlagHandler {
    /**
     * Flag which disable record grouping.
     */
    public FlagNoGroup() {
        super(ImmutableList.of("no-group"));
    }

    @Override
    public boolean acceptsSource(@Nullable CommandSource source) {
        return true;
    }

    @Override
    public boolean acceptsValue(String value) {
        return true;
    }

    @Override
    public Optional<ListenableFuture<?>> process(QuerySession session, String parameter, Optional<String> value, Query query) {
        query.setAggregate(false);
        return Optional.empty();
    }
}
