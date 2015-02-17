package com.example.jaf50.survey.actions;

public class DirectContentTransition implements Action {

  private String fromId;
  private String toId;

  public DirectContentTransition(String fromId, String toId) {
    setFromId(fromId);
    setToId(toId);
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

  @Override
  public void execute() {

  }
}
