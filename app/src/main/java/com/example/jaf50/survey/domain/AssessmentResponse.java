package com.example.jaf50.survey.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

@ParseClassName("AssessmentResponse")
public class AssessmentResponse extends ParseObject {

  public AssessmentResponse() {
  }

  public String getResponseId() {
    return getString("responseId");
  }

  public void setResponseId(String responseId) {
    put("responseId", responseId);
  }

  public List<Object> getValues() {
    return getList("values");
  }

  public void setValues(List<Object> values) {
    remove("values");
    addAll("values", values);
  }

  public void addValue(Object value) {
    add("values", value);
  }

  public Date getResponseDate() {
    return getDate("responseDate");
  }

  public void setResponseDate(Date responseDate) {
    put("responseDate", responseDate);
  }

  public boolean isEmpty() {
    return getValues().isEmpty();
  }

  public boolean containsResponse(AssessmentResponse otherResponse) {
    if (otherResponse == null) {
      return false;
    }

    String thisResponseId = getResponseId();
    String otherResponseId = otherResponse.getResponseId();
    if (thisResponseId != null ? !thisResponseId.equals(otherResponseId) : otherResponseId != null) {
      return false;
    }

    return getValues().containsAll(otherResponse.getValues());
  }

  public boolean equalsResponse(AssessmentResponse otherResponse) {
    if (this == otherResponse) return true;
    if (otherResponse == null || getClass() != otherResponse.getClass()) return false;

    String thisResponseId = getResponseId();
    String otherResponseId = otherResponse.getResponseId();
    if (thisResponseId != null ? !thisResponseId.equals(otherResponseId) : otherResponseId != null) {
      return false;
    }

    List<Object> theseValues = getValues();
    List<Object> otherValues = otherResponse.getValues();
    if (theseValues != null ? !theseValues.equals(otherValues) : otherValues != null) {
      return false;
    }

    return true;
  }
}
