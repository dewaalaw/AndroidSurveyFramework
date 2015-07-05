package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.AssessmentResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class ResponseCriteria {

  @Getter @Setter private boolean isDefault = false;

  private HashMap<String, List<ResponseCondition>> variableToResponseConditions = new HashMap<>();

  public boolean isSatisfied(List<AssessmentResponse> responses) {
    for (String responseId : variableToResponseConditions.keySet()) {
      List<ResponseCondition> responseConditions = variableToResponseConditions.get(responseId);
      AssessmentResponse responseForId = getResponseForId(responseId, responses);

      // Verify each variable response set is satisfied.
      for (ResponseCondition responseCondition : responseConditions) {
        if (!responseCondition.isSatisfied(responseForId)) {
          return false;
        }
      }
    }
    return true;
  }

  private AssessmentResponse getResponseForId(String responseId, List<AssessmentResponse> responses) {
    for (AssessmentResponse response : responses) {
      if (response.getResponseId().equals(responseId)) {
        return response;
      }
    }
    return null;
  }

  public void addCondition(ResponseCondition responseCondition) {
    AssessmentResponse expectedResponse = responseCondition.getExpectedResponse();
    if (variableToResponseConditions.containsKey(expectedResponse.getResponseId())) {
      List<ResponseCondition> currentExpectedResponseConditions = variableToResponseConditions.get(expectedResponse.getResponseId());
      currentExpectedResponseConditions.add(responseCondition);
    } else {
      List<ResponseCondition> conditions = new ArrayList<>();
      conditions.add(responseCondition);
      variableToResponseConditions.put(expectedResponse.getResponseId(), conditions);
    }
  }
}
