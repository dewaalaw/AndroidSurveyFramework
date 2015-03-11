package com.example.jaf50.survey.parser;

public class ResponseCriteriaModel extends ComponentModel {

  private ResponseConditionOperator condition;
  private ResponseModel response;
  private String transition;
  private boolean allowsSkipping;

  public ResponseConditionOperator getCondition() {
    return condition;
  }

  public void setCondition(ResponseConditionOperator condition) {
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

  public boolean allowsSkipping() {
    return allowsSkipping;
  }

  public void setAllowsSkipping(boolean allowsSkipping) {
    this.allowsSkipping = allowsSkipping;
  }
}
