package com.example.jaf50.survey;

import com.example.jaf50.survey.parser.StudyModel;

public class AssessmentHolder {

  private static AssessmentHolder instance;
  private StudyModel studyModel;
  private boolean isAssessmentInProgress;

  private AssessmentHolder() {
  }

  public static AssessmentHolder getInstance() {
    if (instance == null) {
      instance = new AssessmentHolder();
    }
    return instance;
  }

  public void setStudyModel(StudyModel studyModel) {
    this.studyModel = studyModel;
  }

  public StudyModel getStudyModel() {
    return studyModel;
  }

  public boolean isAssessmentInProgress() {
    return isAssessmentInProgress;
  }

  public void setAssessmentInProgress(boolean isAssessmentInProgress) {
    this.isAssessmentInProgress = isAssessmentInProgress;
  }
}
