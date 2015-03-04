package com.example.jaf50.survey.parser;

import com.google.gson.annotations.SerializedName;

public enum ResponseConditionOperator {

  @SerializedName("=")
  EQUALS("="),
  @SerializedName("contains")
  CONTAINS("contains"),
  @SerializedName("default")
  DEFAULT("default");

  private String condition;

  ResponseConditionOperator(String condition) {
    this.condition = condition;
  }

  public String getCondition() {
    return condition;
  }
}
