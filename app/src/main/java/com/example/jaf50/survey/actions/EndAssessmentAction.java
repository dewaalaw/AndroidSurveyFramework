package com.example.jaf50.survey.actions;

import com.example.jaf50.survey.domain.Assessment;

public class EndAssessmentAction implements Action {

  private Assessment assessment;
//  private List<AssessmentResponse> assessmentResponses;
//
  public EndAssessmentAction(Assessment assessment) {
    this.assessment = assessment;
  }
//
//  public void setAssessmentResponses(List<AssessmentResponse> assessmentResponses) {
//    this.assessmentResponses = assessmentResponses;
//  }

  @Override
  public void execute() {
//    assessment.setResponses(assessmentResponses);
//    assessment.save();
  }

  public Assessment getAssessment() {
    return assessment;
  }
}
