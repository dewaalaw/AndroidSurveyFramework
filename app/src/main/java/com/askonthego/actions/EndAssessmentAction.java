package com.askonthego.actions;

import com.askonthego.domain.Assessment;

public class EndAssessmentAction implements Action {

  private Assessment assessment;

  public EndAssessmentAction(Assessment assessment) {
    this.assessment = assessment;
  }

  @Override
  public void execute() {
    // TODO
  }

  public Assessment getAssessment() {
    return assessment;
  }
}
