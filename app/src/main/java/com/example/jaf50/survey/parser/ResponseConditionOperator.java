package com.example.jaf50.survey.parser;

import com.google.gson.annotations.SerializedName;

public enum ResponseConditionOperator {

  @SerializedName("=")
  EQUALS("="),
  @SerializedName("containsAll")
  CONTAINS_ALL("containsAll"),
  @SerializedName("containsAny")
  CONTAINS_ANY("containsAny"),
  @SerializedName("default")
  DEFAULT("default"),
  @SerializedName("complete")
  COMPLETE("complete");

  private String operator;

  ResponseConditionOperator(String operator) {
    this.operator = operator;
  }

  public String getOperator() {
    return operator;
  }
}
