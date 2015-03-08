package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.ResponseConditionOperator;

public class ResponseCondition {

  private ResponseConditionOperator responseConditionOperator;
  private AssessmentResponse expectedResponse;

  public ResponseCondition(ResponseConditionOperator responseConditionOperator, AssessmentResponse expectedResponse) {
    this.responseConditionOperator = responseConditionOperator;
    this.expectedResponse = expectedResponse;
  }

  public AssessmentResponse getExpectedResponse() {
    return expectedResponse;
  }

  public boolean isSatisfied(AssessmentResponse response) {
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
