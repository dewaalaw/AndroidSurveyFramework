package com.example.jaf50.survey.parser;

import com.google.gson.annotations.SerializedName;

public enum ResponseConditionOperator {

  @SerializedName("=")
  EQUALS("="),
  @SerializedName("contains")
  CONTAINS("contains"),
  @SerializedName("default")
  DEFAULT("default");

  private String operator;

  ResponseConditionOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
}
