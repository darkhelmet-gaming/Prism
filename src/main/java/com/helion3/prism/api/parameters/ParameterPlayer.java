package com.helion3.prism.api.parameters;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.profile.GameProfile;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.helion3.prism.Prism;
import com.helion3.prism.api.query.Condition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.utils.DataQueries;

public class ParameterPlayer extends SimpleParameterHandler {
    private final Pattern pattern = Pattern.compile("[\\w,:-]+");

    /**
     * Parameter handling a specific player
     */
    public ParameterPlayer() {
        super(ImmutableList.of("p", "player"));
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
        ListenableFuture<GameProfile> profile = Prism.getGame().getServer().getGameProfileManager().get(value, true);

        profile.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    query.addCondition(Condition.of(DataQueries.Player.toString() + ".$id", MatchRule.EQUALS, profile.get().getUniqueId().toString()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }, MoreExecutors.sameThreadExecutor());

        return Optional.of(profile);
    }
}