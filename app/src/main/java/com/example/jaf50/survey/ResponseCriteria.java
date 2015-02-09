package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.List;

public class ResponseCriteria {

  private List<ResponseCondition> responseConditions = new ArrayList<>();

  public boolean isSatisfied(List<Response> responses) {
    /*
     * example response list:
     *
     * 1, 2, 5 selected in checkboxes.
     *
     * If response contains 1 and 5 then perform a given action.
     * else if response equals 2 then perform a different action.
     */
    for (ResponseCondition responseCondition : responseConditions) {
      if (!responseCondition.isSatisfied(responses)) {
        return false;
      }
    }
    return true;
  }

  public void addCondition(ResponseCondition responseCondition) {
    responseConditions.add(responseCondition);
  }
}
