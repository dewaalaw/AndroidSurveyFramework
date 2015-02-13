package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;

public class Value extends SugarRecord<Value> {

  SurveyResponse response;
  String value;

  public Value() {
  }

  public void setValue(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public SurveyResponse getSurveyResponse() {
    return response;
  }

  public void setSurveyResponse(SurveyResponse response) {
    this.response = response;
  }
}
