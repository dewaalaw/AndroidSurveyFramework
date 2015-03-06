package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.parser.ResponseConditionOperator;

public class ResponseCondition {

  private String operator;
  private SurveyResponse expectedResponse;

  public ResponseCondition(String operator, SurveyResponse expectedResponse) {
    this.operator = operator;
    this.expectedResponse = expectedResponse;
  }

  public SurveyResponse getExpectedResponse() {
    return expectedResponse;
  }

  public boolean isSatisfied(SurveyResponse response) {
    if (operator.equals(ResponseConditionOperator.EQUALS.getOperator())) {
      return expectedResponse.equals(response);
    } else if (operator.equals(ResponseConditionOperator.CONTAINS.getOperator())) {
      return response.contains(expectedResponse);
    } else if (operator.equals(ResponseConditionOperator.DEFAULT.getOperator())) {
      return true;
    } else if (operator.equals(ResponseConditionOperator.COMPLETE.getOperator())) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "ResponseCondition{" +
        "operator='" + operator + '\'' +
        ", expectedResponse=" + expectedResponse +
        '}';
  }
}
