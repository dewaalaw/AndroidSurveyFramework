package com.example.jaf50.survey.parser;

import java.util.List;

public class StudyModel {

  private WelcomeModel welcomeScreen;
  private List<SurveyModel> surveys;

  public WelcomeModel getWelcomeScreen() {
    return welcomeScreen;
  }

  public void setWelcomeScreen(WelcomeModel welcomeScreen) {
    this.welcomeScreen = welcomeScreen;
  }

  public List<SurveyModel> getSurveys() {
    return surveys;
  }

  public void setSurveys(List<SurveyModel> surveys) {
    this.surveys = surveys;
  }
}
