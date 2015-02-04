package com.example.jaf50.survey;

import java.util.List;

public class ResponseAggregator {

  public String collectResponses(List<ISurveyComponent> surveyComponents) {
    StringBuilder builder = new StringBuilder();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        Response response = surveyComponent.getResponse();
        for (String value : response.getValues()) {
          builder.append(value).append(",");
        }
      }
    }
    return builder.toString();
  }
}
