package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class SurveyResponse extends SugarRecord<SurveyResponse> {

  Survey survey;
  String responseId;
  @Ignore
  List<Value> values;

  public SurveyResponse() {
  }

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public List<Value> getValues() {
    if (values == null) {
      values = Value.find(Value.class, "response = ?", getId() + "");
    }
    return values;
  }

  public void setValues(List<Value> values) {
    this.values = values;
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    this.survey = survey;
  }

  @Override
  public void save() {
    super.save();
    for (Value value : values) {
      value.setSurveyResponse(this);
      value.save();
    }
    super.save();
  }
}
