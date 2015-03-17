package com.example.jaf50.survey.actions;

import com.example.jaf50.survey.domain.Assessment;

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
