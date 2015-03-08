package com.example.jaf50.survey.parser;

public class NavigationButtonModel {

  private Boolean allowed;
  private String label;

  public Boolean isAllowed() {
    return allowed;
  }

  public String getLabel() {
    return label;
  }

  public void setAllowed(Boolean allowed) {
    this.allowed = allowed;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
