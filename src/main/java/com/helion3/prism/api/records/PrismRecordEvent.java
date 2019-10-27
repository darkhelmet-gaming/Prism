package com.helion3.prism.api.records;

import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.api.util.annotation.NonnullByDefault;

/**
 * An event to be thrown when a PrismRecord is ready to be saved. Other
 * plugins can catch this event and read the PrismRecord or cancel the
 * event.
 */
public class PrismRecordEvent extends AbstractEvent implements Cancellable {

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
  PrismRecordEvent(PrismRecord prismRecord, Cause cause) {
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

  @Override
  @NonnullByDefault
  public Cause getCause() {
    return this.cause;
  }

  @Override
  @NonnullByDefault
  public Object getSource() {
    return this.prismRecord.getSource();
  }

  public PrismRecord getPrismRecord() {
    return prismRecord;
  }
}
