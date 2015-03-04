package com.example.jaf50.survey.parser;

public class ResponseCriteriaModel extends ComponentModel {

  private ResponseCondition condition;
  private ResponseModel response;
  private String transition;

  public ResponseCondition getCondition() {
    return condition;
  }

  public void setCondition(ResponseCondition condition) {
    this.condition = condition;
  }

  public ResponseModel getResponse() {
    return response;
  }

  public void setResponse(ResponseModel response) {
    this.response = response;
  }

  public String getTransition() {
    return transition;
  }

  public void setTransition(String transition) {
    this.transition = transition;
  }
}
