package com.example.jaf50.survey;

public class ResponseCondition {

  private String operator;
  private Response expectedResponse;

  public ResponseCondition(String operator, Response expectedResponse) {
    this.operator = operator;
    this.expectedResponse = expectedResponse;
  }

  public Response getExpectedResponse() {
    return expectedResponse;
  }

  public boolean isSatisfied(Response response) {
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
