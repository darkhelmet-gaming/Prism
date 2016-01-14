package com.helion3.prism.api.flags;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.command.CommandSource;

import com.google.common.util.concurrent.ListenableFuture;
import com.helion3.prism.api.query.Query;
import com.helion3.prism.api.query.QuerySession;

public interface FlagHandler {
    /**
     * Returns whether this flag is allowed for the current command source.
     *
     * @param source CommandSource of current flag.
     * @return boolean Whether this command source may use this flag.
     */
    boolean acceptsSource(@Nullable CommandSource source);

    /**
     * Returns whether the given value(s) for the handler are acceptable.
     *
     * @param value String Value/input for the parameter
     * @return boolean Whether this value is legal for this parameter.
     */
    boolean acceptsValue(String value);

    /**
     * Returns whether this handler responds to the given flag.
     *
     * @param flag String Flag to check against
     * @return boolean Whether this handler responds to an flag.
     */
    boolean handles(String flag);

    /**
     * Processes the given value into conditions which are then
     * appended to the query.
     *
     * @param session Current Query Session
     * @param flag String flag used
     * @param value String value(s) given with flag
     * @param query Query Current query object
     */
    Optional<ListenableFuture<?>> process(QuerySession session, String flag, Optional<String> value, Query query);
}
