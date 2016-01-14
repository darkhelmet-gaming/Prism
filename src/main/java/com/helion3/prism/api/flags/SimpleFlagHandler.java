package com.helion3.prism.api.flags;

import com.google.common.collect.ImmutableList;

public abstract class SimpleFlagHandler implements FlagHandler {
    private final ImmutableList<String> aliases;

    /**
     * Super constructor for most flag handlers.
     * @param aliases ImmutableList<String> Flags which match this handler
     */
    public SimpleFlagHandler(ImmutableList<String> aliases) {
        this.aliases = aliases;
    }

    /**
     * Returns whether this handler applies to the given
     * parameter string.
     */
    @Override
    public boolean handles(String alias) {
        return aliases.contains(alias);
    }
}
