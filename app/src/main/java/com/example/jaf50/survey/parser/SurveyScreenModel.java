package com.example.jaf50.survey.parser;

import java.util.ArrayList;
import java.util.List;

public class SurveyScreenModel {

  private String id;
  private String mainText;
  private NavigationButtonModel previous;
  private NavigationButtonModel next;
  private List<ComponentModel> components;
  private List<ResponseCriteriaModel> responseCriteria = new ArrayList<>();

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getMainText() {
    return mainText;
  }

  public void setMainText(String mainText) {
    this.mainText = mainText;
  }

  public List<ComponentModel> getComponents() {
    return components;
  }

  public void setComponents(List<ComponentModel> components) {
    this.components = components;
  }

  public List<ResponseCriteriaModel> getResponseCriteria() {
    return responseCriteria;
  }

  public void setResponseCriteria(List<ResponseCriteriaModel> responseCriteria) {
    this.responseCriteria = responseCriteria;
  }

  public NavigationButtonModel getPrevious() {
    return previous;
  }

  public NavigationButtonModel getNext() {
    return next;
  }
}
