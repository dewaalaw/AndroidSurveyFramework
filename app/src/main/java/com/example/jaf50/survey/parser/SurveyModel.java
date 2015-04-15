package com.example.jaf50.survey.parser;

import java.util.List;

public class SurveyModel {

  private String description;
  private String name;
  private int timeoutMinutes;
  private List<SurveyScreenModel> screens;

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<SurveyScreenModel> getScreens() {
    return screens;
  }

  public void setScreens(List<SurveyScreenModel> screens) {
    this.screens = screens;
  }

  public int getTimeoutMinutes() {
    return timeoutMinutes;
  }

  public void setTimeoutMinutes(int timeoutMinutes) {
    this.timeoutMinutes = timeoutMinutes;
  }
}
