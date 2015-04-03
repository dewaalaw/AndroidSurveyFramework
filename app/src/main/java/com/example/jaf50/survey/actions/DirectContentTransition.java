package com.example.jaf50.survey.actions;

public class DirectContentTransition implements Action {

  private String fromId;
  private String toId;
  private boolean requiresResponse;

  public DirectContentTransition(String fromId, String toId, boolean requiresResponse) {
    setFromId(fromId);
    setToId(toId);
    setRequiresResponse(requiresResponse);
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

  public boolean requiresResponse() {
    return requiresResponse;
  }

  public void setRequiresResponse(boolean requiresResponse) {
    this.requiresResponse = requiresResponse;
  }

  @Override
  public void execute() {
    // TODO
  }
}
