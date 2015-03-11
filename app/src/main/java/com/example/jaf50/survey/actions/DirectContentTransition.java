package com.example.jaf50.survey.actions;

public class DirectContentTransition implements Action {

  private String fromId;
  private String toId;
  private boolean allowsSkipping;

  public DirectContentTransition(String fromId, String toId, boolean allowsSkipping) {
    setFromId(fromId);
    setToId(toId);
    setAllowsSkipping(allowsSkipping);
  }

  public void setFromId(String fromId) {
    this.fromId = fromId;
  }

  public void setToId(String toId) {
    this.toId = toId;
  }

  public String getFromId() {
    return fromId;
  }

  public String getToId() {
    return toId;
  }

  public boolean allowsSkipping() {
    return allowsSkipping;
  }

  public void setAllowsSkipping(boolean allowsSkipping) {
    this.allowsSkipping = allowsSkipping;
  }

  @Override
  public void execute() {
    // TODO
  }
}
