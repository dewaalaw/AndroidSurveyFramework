package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.List;

public class ContentTransitioner {

  private List<ContentTransition> contentTransitions = new ArrayList<ContentTransition>();
  private ContentTransitionListener contentTransitionListener;

  public void setContentTransitionListener(ContentTransitionListener contentTransitionListener) {
    this.contentTransitionListener = contentTransitionListener;
  }

  public void addTransition(ContentTransition contentTransition) {
    contentTransitions.add(contentTransition);
  }

  public void executeNextTransition(SurveyScreen screen) {
    // TODO - conditional transition based on responses for the given screen.
    for (ContentTransition contentTransition: contentTransitions) {
      // TODO - this is a naive assumption that there will be exactly one transition with the screen id.
      if (contentTransition.getFromId().equals(screen.getScreenId())) {
        contentTransitionListener.onTransition(contentTransition);
        break;
      }
    }
  }
}
