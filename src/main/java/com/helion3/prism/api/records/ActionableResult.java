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
package com.helion3.prism.api.records;

import java.util.Optional;

import javax.annotation.Nullable;

import org.spongepowered.api.data.Transaction;

public class ActionableResult {
    private final boolean changeWasApplied;
    private final SkipReason skipReason;
    private final Transaction<?> transaction;

    /**
     * Build a skipped actionable result.
     * @param skipReason Reason for skip.
     * @return ActionableResult
     */
    public static ActionableResult skipped(SkipReason skipReason) {
        return new ActionableResult(skipReason);
    }

    /**
     * Build a successful actionable result.
     * @param transaction
     * @return
     */
    public static ActionableResult success(@Nullable Transaction<?> transaction) {
        return new ActionableResult(transaction);
    }

    private ActionableResult(@Nullable Transaction<?> transaction) {
        this.transaction = transaction;
        this.changeWasApplied = true;
        this.skipReason = null;
    }

    private ActionableResult(SkipReason skipReason) {
        this.transaction = null;
        this.changeWasApplied = false;
        this.skipReason = skipReason;
    }

    /**
     * Get if actionable was applied.
     * @return If actionable was applied.
     */
    public boolean applied() {
        return changeWasApplied;
    }

    /**
     * Get any resulting transaction, useful for reversals.
     * @return Optional transaction.
     */
    public Optional<Transaction<?>> getTransaction() {
        return Optional.ofNullable(transaction);
    }

    /**
     * Returns the skip reason, if any.
     * @return SKIP_REASON Reason a change was skipped.
     */
    public SkipReason getSkipReason() {
        return skipReason;
    }
}
