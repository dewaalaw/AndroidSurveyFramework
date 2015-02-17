package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SurveyResponse extends SugarRecord<SurveyResponse> {

  Survey survey;
  Date responseDate;
  String responseId;
  @Ignore
  List<Value> values = new ArrayList<>();

  public SurveyResponse() {
  }

  public SurveyResponse(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }

  public SurveyResponse setResponseId(String responseId) {
    this.responseId = responseId;
    return this;
  }

  public List<Value> getValues() {
    if (values == null || values.isEmpty()) {
      values = Value.find(Value.class, "response = ?", getId() + "");
    }
    return values;
  }

  public void setValues(List<Value> values) {
    for (Value value : values) {
      value.setSurveyResponse(this);
    }
    this.values = values;
  }

  public SurveyResponse addValue(Value value) {
    this.values.add(value);
    value.setSurveyResponse(this);
    return this;
  }

  public SurveyResponse addValue(Object value) {
    Value v = new Value().setValue(value.toString());
    this.values.add(v);
    v.setSurveyResponse(this);
    return this;
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    this.survey = survey;
  }

  public Date getResponseDate() {
    return responseDate;
  }

  public void setResponseDate(Date responseDate) {
    this.responseDate = responseDate;
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

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public boolean contains(SurveyResponse otherResponse) {
    if (otherResponse == null) {
      return false;
    }
    return values.containsAll(otherResponse.getValues());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    SurveyResponse that = (SurveyResponse) o;

    if (responseId != null ? !responseId.equals(that.responseId) : that.responseId != null)
      return false;
    if (values != null ? !values.equals(that.values) : that.values != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = survey != null ? survey.hashCode() : 0;
    result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
    result = 31 * result + (responseId != null ? responseId.hashCode() : 0);
    result = 31 * result + (values != null ? values.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SurveyResponse{" +
        "survey=" + survey +
        ", responseDate=" + responseDate +
        ", responseId='" + responseId + '\'' +
        ", values=" + values +
        '}';
  }
}
