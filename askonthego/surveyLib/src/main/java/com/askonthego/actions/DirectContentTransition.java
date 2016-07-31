package com.askonthego.actions;

import lombok.Getter;
import lombok.Setter;

public class DirectContentTransition implements Action {

  @Getter @Setter private String fromId;
  @Getter @Setter private String toId;
  @Getter @Setter private boolean responseRequired;

  public DirectContentTransition(String fromId, String toId, boolean responseRequired) {
    setFromId(fromId);
    setToId(toId);
    setResponseRequired(responseRequired);
  }

  @Override
  public void execute() {
    // TODO
  }
}
