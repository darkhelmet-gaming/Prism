package com.helion3.prism.api.services;

import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nonnull;

public interface PrismService {

  /**
   * Queries all events matching the conditions in the {@link Request} and restores the
   * original states within every event.
   *
   * @param source The source requesting the rollback maneuver
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void rollback(@Nonnull CommandSource source, @Nonnull Request conditions) throws Exception;

  /**
   * Queries all events matching the conditions in the {@link Request} and restores the
   * final states within every event.
   *
   * @param source The source requesting the restoration maneuver
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void restore(@Nonnull CommandSource source, @Nonnull Request conditions) throws Exception;

  /**
   * Queries all events matching the conditions in the {@link Request} and sends
   * the command source the matching information.
   *
   * @param source The source requesting the information
   * @param conditions The collection of all parameters with which to filter out logged events
   * @throws Exception if something unexpected happens
   */
  void lookup(@Nonnull CommandSource source, @Nonnull Request conditions) throws Exception;

}
