package com.helion3.prism.api.services;

import com.google.common.base.Preconditions;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import com.helion3.prism.api.data.PrismEvent;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * A store for all filtering information necessary to locate logged events.
 */
public final class Request implements Serializable {

  private static final long serialVersionUID = 4369983541428028962L;

  /**
   * Create a {@link Request.Builder} to construct a {@link Request} to use in a {@link PrismService}.
   *
   * @return a builder
   */
  @Nonnull
  public static Builder builder() {
    return new Builder();
  }

  private final Set<PrismEvent> events;
  private final Set<String> targets;
  private final Set<UUID> playerUuids;
  private final Set<UUID> worldUuids;
  private final Range<Integer> xRange;
  private final Range<Integer> yRange;
  private final Range<Integer> zRange;
  private final Date earliest;
  private final Date latest;

  private final Set<Object> flags;

  private Request(@Nonnull Set<PrismEvent> events,
                  @Nonnull Set<String> targets,
                  @Nonnull Set<UUID> playerUuids,
                  @Nonnull Set<UUID> worldUuids,
                  Range<Integer> xRange,
                  Range<Integer> yRange,
                  Range<Integer> zRange,
                  Date earliest,
                  Date latest,
                  @Nonnull Set<Object> flags) {
    this.events = events;
    this.targets = targets;
    this.playerUuids = playerUuids;
    this.worldUuids = worldUuids;
    this.xRange = xRange;
    this.yRange = yRange;
    this.zRange = zRange;
    this.earliest = earliest;
    this.latest = latest;
    this.flags = flags;
  }

  @Nonnull
  public Set<PrismEvent> getEvents() {
    return events;
  }

  @Nonnull
  public Set<String> getTargets() {
    return targets;
  }

  @Nonnull
  public Set<UUID> getPlayerUuids() {
    return playerUuids;
  }

  @Nonnull
  public Set<UUID> getWorldUuids() {
    return worldUuids;
  }

  @Nonnull
  public Optional<Range<Integer>> getxRange() {
    return Optional.ofNullable(xRange);
  }

  @Nonnull
  public Optional<Range<Integer>> getyRange() {
    return Optional.ofNullable(yRange);
  }

  @Nonnull
  public Optional<Range<Integer>> getzRange() {
    return Optional.ofNullable(zRange);
  }

  @Nonnull
  public Optional<Date> getEarliest() {
    return Optional.ofNullable(earliest);
  }

  @Nonnull
  public Optional<Date> getLatest() {
    return Optional.ofNullable(latest);
  }

  @Nonnull
  public Set<Object> getFlags() {
    return flags;
  }

  public static class Builder {

    private Set<PrismEvent> events = Sets.newHashSet();
    private Set<String> targets = Sets.newHashSet();
    private Set<UUID> playerUuids = Sets.newHashSet();
    private Set<UUID> worldUuids = Sets.newHashSet();
    private Range<Integer> xRange = null;
    private Range<Integer> yRange = null;
    private Range<Integer> zRange = null;
    private Date earliest = null;
    private Date latest = null;
    private Set<Object> flags = Sets.newHashSet();

    private Builder() {
    }

    public Request build() {
      return new Request(events, targets, playerUuids, worldUuids, xRange, yRange, zRange, earliest, latest, flags);
    }

    @SuppressWarnings("unused")
    public Builder addEvent(@Nonnull PrismEvent event) {
      Preconditions.checkNotNull(event);
      this.events.add(event);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addTarget(@Nonnull String target) {
      Preconditions.checkNotNull(target);
      this.targets.add(target);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addPlayerUuid(@Nonnull UUID playerUuid) {
      Preconditions.checkNotNull(playerUuid);
      this.playerUuids.add(playerUuid);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addWorldUuid(@Nonnull UUID worldUuid) {
      Preconditions.checkNotNull(worldUuid);
      this.worldUuids.add(worldUuid);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setxRange(int lower, int upper) {
      this.xRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setyRange(int lower, int upper) {
      this.yRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setzRange(int lower, int upper) {
      this.zRange = Range.closed(lower, upper);
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setEarliest(Date earliest) {
      this.earliest = earliest;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder setLatest(Date latest) {
      this.latest = latest;
      return this;
    }

    @SuppressWarnings("unused")
    public Builder addFlag(@Nonnull Object flag) {
      Preconditions.checkNotNull(flag);
      this.flags.add(flag);
      return this;
    }

  }

}
