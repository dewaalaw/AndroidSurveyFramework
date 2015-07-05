package com.example.jaf50.survey;

import com.example.jaf50.survey.parser.StudyModel;

import lombok.Getter;
import lombok.Setter;

public class AssessmentHolder {

  private static AssessmentHolder instance;
  @Getter @Setter private StudyModel studyModel;
  @Getter @Setter private boolean assessmentInProgress;

  private AssessmentHolder() {}

  public static AssessmentHolder getInstance() {
    if (instance == null) {
      instance = new AssessmentHolder();
    }
    return instance;
  }
}
