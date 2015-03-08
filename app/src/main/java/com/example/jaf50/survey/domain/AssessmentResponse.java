package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentResponse extends SugarRecord<AssessmentResponse> {

  Assessment assessment;
  Date responseDate;
  String responseId;
  @Ignore
  List<Value> values = new ArrayList<>();

  public AssessmentResponse() {
  }

  public AssessmentResponse(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }

  public AssessmentResponse setResponseId(String responseId) {
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
      value.setResponse(this);
    }
    this.values = values;
  }

  public AssessmentResponse addValue(Value value) {
    this.values.add(value);
    value.setResponse(this);
    return this;
  }

  public AssessmentResponse addValue(Object value) {
    Value v = new Value().setValue(value.toString());
    this.values.add(v);
    v.setResponse(this);
    return this;
  }

  public Assessment getAssessment() {
    return assessment;
  }

  public void setAssessment(Assessment assessment) {
    this.assessment = assessment;
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
      value.setResponse(this);
      value.save();
    }
    super.save();
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public boolean contains(AssessmentResponse otherResponse) {
    if (otherResponse == null) {
      return false;
    }
    return values.containsAll(otherResponse.getValues());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    AssessmentResponse that = (AssessmentResponse) o;

    if (responseId != null ? !responseId.equals(that.responseId) : that.responseId != null)
      return false;
    if (values != null ? !values.equals(that.values) : that.values != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = assessment != null ? assessment.hashCode() : 0;
    result = 31 * result + (responseDate != null ? responseDate.hashCode() : 0);
    result = 31 * result + (responseId != null ? responseId.hashCode() : 0);
    result = 31 * result + (values != null ? values.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "SurveyResponse{" +
        "survey=" + assessment +
        ", responseDate=" + responseDate +
        ", responseId='" + responseId + '\'' +
        ", values=" + values +
        '}';
  }
}
