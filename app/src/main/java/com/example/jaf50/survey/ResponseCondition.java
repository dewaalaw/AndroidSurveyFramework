package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ResponseCondition {

  private String operator;
  private List<Response> expectedResponses;

  public ResponseCondition(String operator, Response expectedResponse) {
    this(operator, Arrays.asList(expectedResponse));
  }

  public ResponseCondition(String operator, List<Response> expectedResponses) {
    this.operator = operator;
    this.expectedResponses = expectedResponses;
  }

  /*
   * example response list:
   *
   * 1, 2, 5 selected in checkboxes.
   *
   * If response contains 1 and 5 then perform a given action.
   * else if response equals 2 then perform a different action.
   */
  public boolean isSatisfied(List<Response> actualResponses) {
    List <Response> filteredResponses = new ArrayList<>();
    for (Response response : actualResponses) {
      if (!response.isEmpty()) {
        filteredResponses.add(response);
      }
    }

    if (operator.equals("=")) {
      return expectedResponses.equals(filteredResponses);
    } else if (operator.equals("contains")) {

    }
    return false;
  }
}
