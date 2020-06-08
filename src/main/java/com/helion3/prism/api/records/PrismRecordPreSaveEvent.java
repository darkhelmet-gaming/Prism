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

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;

import javax.annotation.Nonnull;

/**
 * An event to be thrown when a PrismRecord is ready to be saved. Other
 * plugins can catch this event and read the PrismRecord or cancel the
 * event.
 */
public class PrismRecordPreSaveEvent extends AbstractEvent implements Cancellable {

  private boolean cancelled = false;
  private final PrismRecord prismRecord;
  private final Cause cause;

  /**
   * The constructor for a prism event to log the creation of a Prism Event
   * to Sponge.
   *
   * @param prismRecord The record sent through the event
   * @param cause       The cause of the event
   */
  public PrismRecordPreSaveEvent(PrismRecord prismRecord, Cause cause) {
    this.prismRecord = prismRecord;
    this.cause = cause;
  }

  @Override
  public boolean isCancelled() {
    return cancelled;
  }

  @Override
  public void setCancelled(boolean cancel) {
    this.cancelled = cancel;
  }

  @Nonnull
  @Override
  public Cause getCause() {
    return this.cause;
  }

  @Nonnull
  @Override
  public Object getSource() {
    return this.prismRecord.getSource();
  }

  /**
   * Getter for the Prism Record saved to this event.
   *
   * @return the saved PrismRecord
   */
  public PrismRecord getPrismRecord() {
    return prismRecord;
  }

}
