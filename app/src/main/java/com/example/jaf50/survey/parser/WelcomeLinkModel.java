package com.example.jaf50.survey.parser;

public class WelcomeLinkModel {

  private String surveyName;
  private String label;
  private String transitionText;
  private String icon;
  private String buttonType;

  public String getSurveyName() {
    return surveyName;
  }

  public void setSurveyName(String surveyName) {
    this.surveyName = surveyName;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }

  public String getTransitionText() {
    return transitionText;
  }

  public void setTransitionText(String transitionText) {
    this.transitionText = transitionText;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }

  public String getButtonType() {
    return buttonType;
  }

  public void setButtonType(String buttonType) {
    this.buttonType = buttonType;
  }
}
