package com.example.jaf50.survey.parser;

import com.google.gson.annotations.SerializedName;

public enum ResponseCondition {

  @SerializedName("=")
  EQUALS("="),
  @SerializedName("contains")
  CONTAINS("contains"),
  @SerializedName("default")
  DEFAULT("default");

  private String condition;

  ResponseCondition(String condition) {
    this.condition = condition;
  }

  public String getCondition() {
    return condition;
  }
}
