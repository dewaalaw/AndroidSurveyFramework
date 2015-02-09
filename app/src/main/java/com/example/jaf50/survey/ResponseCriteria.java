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
      Response responseForId = getResponseForId(responseId, responses);

      // Verify each variable response set is satisfied.
      for (ResponseCondition responseCondition : responseConditions) {
        if (!responseCondition.isSatisfied(responseForId)) {
          return false;
        }
      }
    }
    return true;
  }

  private Response getResponseForId(String responseId, List<Response> responses) {
    for (Response response : responses) {
      if (response.getId().equals(responseId)) {
        return response;
      }
    }
    return null;
  }

  public void addCondition(ResponseCondition responseCondition) {
    Response expectedResponse = responseCondition.getExpectedResponse();
    if (variableToResponseConditions.containsKey(expectedResponse.getId())) {
      List<ResponseCondition> currentExpectedResponseConditions = variableToResponseConditions.get(expectedResponse.getId());
      currentExpectedResponseConditions.add(responseCondition);
    } else {
      List<ResponseCondition> conditions = new ArrayList<>();
      conditions.add(responseCondition);
      variableToResponseConditions.put(expectedResponse.getId(), conditions);
    }
  }
}
