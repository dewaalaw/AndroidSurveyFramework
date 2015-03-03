package com.example.jaf50.survey.parser;

import java.util.List;

public class SurveyScreenModel {

  private String id;
  private List<ComponentModel> components;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<ComponentModel> getComponents() {
    return components;
  }

  public void setComponents(List<ComponentModel> components) {
    this.components = components;
  }
}
