package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResponseCriteria {

  private HashMap<String, List<ResponseCondition>> variableToResponseConditions = new HashMap<>();

  public boolean isSatisfied(List<Response> responses) {
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
      List<Response> responsesForId = getResponsesForId(responseId, responses);

      // Verify each variable response set is satisfied.
      for (ResponseCondition responseCondition : responseConditions) {
        if (!responseCondition.isSatisfied(responsesForId)) {
          return false;
        }
      }
    }
    return true;
  }

  private List<Response> getResponsesForId(String responseId, List<Response> responses) {
    List<Response> responsesForId = new ArrayList<>();
    for (Response response : responses) {
      if (response.getId().equals(responseId)) {
        responsesForId.add(response);
      }
    }
    return responsesForId;
  }

  public void addCondition(ResponseCondition responseCondition) {
    List<Response> expectedResponses = responseCondition.getExpectedResponses();
    if (expectedResponses.size() > 0) {
      String responseId = expectedResponses.get(0).getId();
      if (variableToResponseConditions.containsKey(responseId)) {
        List<ResponseCondition> currentExpectedResponseConditions = variableToResponseConditions.get(responseId);
        currentExpectedResponseConditions.add(responseCondition);
      } else {
        List<ResponseCondition> conditions = new ArrayList<>();
        conditions.add(responseCondition);
        variableToResponseConditions.put(responseId, conditions);
      }
    }
  }
}
