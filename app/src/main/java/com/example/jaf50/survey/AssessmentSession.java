package com.example.jaf50.survey;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.Participant;

public class AssessmentSession {

  private static AssessmentSession instance;

  private Assessment assessment;
  private Participant participant;

  private AssessmentSession() {
  }

  public static AssessmentSession getInstance() {
    if (instance == null) {
      instance = new AssessmentSession();
    }
    return instance;
  }

  public Assessment getAssessment() {
    return assessment;
  }

  public void setAssessment(Assessment assessment) {
    this.assessment = assessment;
  }

  public Participant getParticipant() {
    return participant;
  }

  public void setParticipant(Participant participant) {
    this.participant = participant;
  }
}
