package com.askonthego.response;

import com.askonthego.domain.AssessmentResponse;
import com.askonthego.parser.ResponseConditionOperator;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
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
            return expectedResponse.equalsResponse(response);
        } else if (responseConditionOperator.equals(ResponseConditionOperator.CONTAINS_ALL)) {
            return response.containsAllResponses(expectedResponse);
        } else if (responseConditionOperator.equals(ResponseConditionOperator.CONTAINS_ANY)) {
            return response.containsAnyResponses(expectedResponse);
        } else if (responseConditionOperator.equals(ResponseConditionOperator.DEFAULT)) {
            return true;
        } else if (responseConditionOperator.equals(ResponseConditionOperator.COMPLETE)) {
            return true;
        }
        return false;
    }
}
