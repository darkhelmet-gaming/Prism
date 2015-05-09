package com.helion3.prism.api.results;

public class ActionableResult {

    private final boolean changeWasApplied;

    /**
     * Construct an ActionableResult
     * @param changeWasApplied Whether or not a change was successfully applied.
     */
    public ActionableResult(boolean changeWasApplied) {
        this.changeWasApplied = changeWasApplied;
    }

    /**
     * Returns whether or not a change was successfully applied.
     * @return Whether or not a change was successfully applied.
     */
    public boolean applied() {
        return changeWasApplied;
    }
}
