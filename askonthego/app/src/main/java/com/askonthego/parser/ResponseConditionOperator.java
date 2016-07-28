package com.askonthego.parser;

import com.google.gson.annotations.SerializedName;

public enum ResponseConditionOperator {

  @SerializedName("containsAll") CONTAINS_ALL("containsAll", true),
  @SerializedName("containsAny") CONTAINS_ANY("containsAny", true),
  @SerializedName("=") EQUALS("=", true),
  @SerializedName("default") DEFAULT("default", false),
  @SerializedName("complete") COMPLETE("complete", false);

  private String operator;
  private boolean comparisonOperator;

  ResponseConditionOperator(String operator, boolean comparisonOperator) {
    this.operator = operator;
    this.comparisonOperator = comparisonOperator;
  }

  public String getOperator() {
    return operator;
  }

  public boolean isComparisonOperator() {
    return comparisonOperator;
  }
}
