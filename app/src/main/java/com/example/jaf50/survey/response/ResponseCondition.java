package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.parser.ResponseConditionOperator;

public class ResponseCondition {

  private ResponseConditionOperator responseConditionOperator;
  private SurveyResponse expectedResponse;

  public ResponseCondition(ResponseConditionOperator responseConditionOperator, SurveyResponse expectedResponse) {
    this.responseConditionOperator = responseConditionOperator;
    this.expectedResponse = expectedResponse;
  }

  public SurveyResponse getExpectedResponse() {
    return expectedResponse;
  }

  public boolean isSatisfied(SurveyResponse response) {
    if (responseConditionOperator.equals(ResponseConditionOperator.EQUALS)) {
      return expectedResponse.equals(response);
    } else if (responseConditionOperator.equals(ResponseConditionOperator.CONTAINS)) {
      return response.contains(expectedResponse);
    } else if (responseConditionOperator.equals(ResponseConditionOperator.DEFAULT)) {
      return true;
    } else if (responseConditionOperator.equals(ResponseConditionOperator.COMPLETE)) {
      return true;
    }
    return false;
  }

  @Override
  public String toString() {
    return "ResponseCondition{" +
        "responseConditionOperator='" + responseConditionOperator + '\'' +
        ", expectedResponse=" + expectedResponse +
        '}';
  }
}
