package com.example.jaf50.survey.response;

import com.example.jaf50.survey.domain.SurveyResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseCriteria {

  private HashMap<String, List<ResponseCondition>> variableToResponseConditions = new HashMap<>();

  public boolean isSatisfied(List<SurveyResponse> responses) {
    /*
     * example response list:
     *
     * 1, 2, 5 selected in checkboxes.
     *
     * If response contains 1 and 5 then perform a given action.
     * else if response equals 2 then perform a different action.
     */
    for (String responseId : variableToResponseConditions.keySet()) {
      List<ResponseCondition> responseConditions = variableToResponseConditions.get(responseId);
      SurveyResponse responseForId = getResponseForId(responseId, responses);

      // Verify each variable response set is satisfied.
      for (ResponseCondition responseCondition : responseConditions) {
        if (!responseCondition.isSatisfied(responseForId)) {
          return false;
        }
      }
    }
    return true;
  }

  private SurveyResponse getResponseForId(String responseId, List<SurveyResponse> responses) {
    for (SurveyResponse response : responses) {
      if (response.getResponseId().equals(responseId)) {
        return response;
      }
    }
    return null;
  }

  public void addCondition(ResponseCondition responseCondition) {
    SurveyResponse expectedResponse = responseCondition.getExpectedResponse();
    if (variableToResponseConditions.containsKey(expectedResponse.getId())) {
      List<ResponseCondition> currentExpectedResponseConditions = variableToResponseConditions.get(expectedResponse.getId());
      currentExpectedResponseConditions.add(responseCondition);
    } else {
      List<ResponseCondition> conditions = new ArrayList<>();
      conditions.add(responseCondition);
      variableToResponseConditions.put(expectedResponse.getResponseId(), conditions);
    }
  }
}
