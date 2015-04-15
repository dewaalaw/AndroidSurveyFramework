package com.example.jaf50.survey.domain;

public class AssessmentSaveOptions {

  private boolean isTimeout;

  public boolean isTimeout() {
    return isTimeout;
  }

  public AssessmentSaveOptions setTimeout(boolean isTimeout) {
    this.isTimeout = isTimeout;
    return this;
  }
}
