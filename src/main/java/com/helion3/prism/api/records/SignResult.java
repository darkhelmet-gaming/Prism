package com.helion3.prism.api.records;

public class SignResult extends ResultComplete implements Actionable {
  @Override
  public ActionableResult rollback() throws Exception {

    // TODO implement
    return ActionableResult.skipped(SkipReason.UNIMPLEMENTED);

  }

  @Override
  public ActionableResult restore() throws Exception {

    // TODO implement
    return ActionableResult.skipped(SkipReason.UNIMPLEMENTED);

  }
}
