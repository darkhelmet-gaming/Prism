package com.helion3.prism.api.services;

import com.helion3.prism.api.flags.Flag;
import com.helion3.prism.api.query.ConditionGroup;
import com.helion3.prism.api.query.FieldCondition;
import com.helion3.prism.api.query.MatchRule;
import com.helion3.prism.api.query.QuerySession;
import com.helion3.prism.api.query.Sort;
import com.helion3.prism.commands.ApplierCommand;
import com.helion3.prism.util.AsyncUtil;
import com.helion3.prism.util.DataQueries;
import org.spongepowered.api.command.CommandSource;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;

public class PrismServiceImpl implements PrismService {

  @Override
  public void rollback(@Nonnull CommandSource source, @Nonnull Request conditions) {
    QuerySession session = buildSession(source, conditions);
    session.addFlag(Flag.NO_GROUP);
    ApplierCommand.runApplier(session, Sort.NEWEST_FIRST);
  }

  @Override
  public void restore(@Nonnull CommandSource source, @Nonnull Request conditions) {
    QuerySession session = buildSession(source, conditions);
    session.addFlag(Flag.NO_GROUP);
    ApplierCommand.runApplier(session, Sort.OLDEST_FIRST);
  }

  @Override
  public void lookup(@Nonnull CommandSource source, @Nonnull Request conditions) {
    AsyncUtil.lookup(buildSession(source, conditions));
  }

  private QuerySession buildSession(CommandSource source, Request conditions) {
    final QuerySession session = new QuerySession(source);
    com.helion3.prism.api.query.Query query = session.newQuery();

    ConditionGroup eventConditionGroup = new ConditionGroup(ConditionGroup.Operator.OR);
    conditions.getEvents().forEach(event ->
        eventConditionGroup.add(FieldCondition.of(
            DataQueries.EventName,
            MatchRule.EQUALS,
            event.getId())));
    if (!eventConditionGroup.getConditions().isEmpty()) {
      query.addCondition(eventConditionGroup);
    }

    ConditionGroup targetConditionGroup = new ConditionGroup(ConditionGroup.Operator.OR);
    conditions.getTargets().forEach(target ->
        targetConditionGroup.add(FieldCondition.of(
            DataQueries.Target,
            MatchRule.EQUALS,
            Pattern.compile(target.replace("_", " ")))));
    if (!targetConditionGroup.getConditions().isEmpty()) {
      query.addCondition(targetConditionGroup);
    }

    ConditionGroup playerConditionGroup = new ConditionGroup(ConditionGroup.Operator.OR);
    conditions.getPlayerUuids().forEach(uuid ->
        playerConditionGroup.add(FieldCondition.of(
            DataQueries.Player,
            MatchRule.EQUALS,
            uuid.toString())));
    if (!playerConditionGroup.getConditions().isEmpty()) {
      query.addCondition(playerConditionGroup);
    }

    ConditionGroup worldConditionGroup = new ConditionGroup(ConditionGroup.Operator.OR);
    conditions.getWorldUuids().forEach(uuid ->
        worldConditionGroup.add(FieldCondition.of(
            DataQueries.Location.then(DataQueries.WorldUuid),
            MatchRule.EQUALS,
            uuid.toString())));
    if (!worldConditionGroup.getConditions().isEmpty()) {
      query.addCondition(worldConditionGroup);
    }

    conditions.getxRange().ifPresent(range -> {
      query.addCondition(FieldCondition.of(
          DataQueries.X,
          MatchRule.GREATER_THAN_EQUAL,
          range.lowerEndpoint()));
      query.addCondition(FieldCondition.of(
          DataQueries.X,
          MatchRule.LESS_THAN_EQUAL,
          range.upperEndpoint()));
    });
    conditions.getyRange().ifPresent(range -> {
      query.addCondition(FieldCondition.of(
          DataQueries.Y,
          MatchRule.GREATER_THAN_EQUAL,
          range.lowerEndpoint()));
      query.addCondition(FieldCondition.of(
          DataQueries.Y,
          MatchRule.LESS_THAN_EQUAL,
          range.upperEndpoint()));
    });
    conditions.getzRange().ifPresent(range -> {
      query.addCondition(FieldCondition.of(
          DataQueries.Z,
          MatchRule.GREATER_THAN_EQUAL,
          range.lowerEndpoint()));
      query.addCondition(FieldCondition.of(
          DataQueries.Z,
          MatchRule.LESS_THAN_EQUAL,
          range.upperEndpoint()));
    });
    conditions.getEarliest().ifPresent(earliest ->
        query.addCondition(FieldCondition.of(
            DataQueries.Created,
            MatchRule.GREATER_THAN_EQUAL,
            earliest)));
    conditions.getLatest().ifPresent(latest ->
        query.addCondition(FieldCondition.of(
            DataQueries.Created,
            MatchRule.LESS_THAN_EQUAL,
            latest)));
    conditions.getFlags()
        .stream()
        .filter(o -> o instanceof Flag)
        .map(o -> (Flag) o)
        .forEach(session::addFlag);
    return session;
  }
}
