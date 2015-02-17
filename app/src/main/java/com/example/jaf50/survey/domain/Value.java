package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;

public class Value extends SugarRecord<Value> {

  SurveyResponse response;
  String value;

  public Value() {
  }

  public Value setValue(String value) {
    this.value = value;
    return this;
  }

  public String getValue() {
    return value;
  }

  public Value setSurveyResponse(SurveyResponse response) {
    this.response = response;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Value value1 = (Value) o;

    if (value != null ? !value.equals(value1.value) : value1.value != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = response != null ? response.hashCode() : 0;
    result = 31 * result + (value != null ? value.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Value{" +
        "response=" + response +
        ", value='" + value + '\'' +
        '}';
  }
}
