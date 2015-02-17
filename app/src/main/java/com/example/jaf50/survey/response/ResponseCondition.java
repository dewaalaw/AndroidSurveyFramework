package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.SurveyResponse;

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
    if (operator.equals("=")) {
      return expectedResponse.equals(response);
    } else if (operator.equals("contains")) {
      return response.contains(expectedResponse);
    } else if (operator.equals("default")) {
      return true;
    }
    return false;
  }
}
