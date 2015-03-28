package com.example.jaf50.survey.service;

import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.response.Response;
import com.example.jaf50.survey.ui.ISurveyComponent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseCollectorService {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

  public List<AssessmentResponse> collectResponses(List<ISurveyComponent> surveyComponents) {
    List<AssessmentResponse> responses = new ArrayList<>();
    Date responseDate = new Date();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        AssessmentResponse assessmentResponse = new AssessmentResponse();
        Response response = surveyComponent.getResponse();
        assessmentResponse.setResponseDate(dateFormatter.format(responseDate));
        assessmentResponse.setResponseId(response.getId());
        assessmentResponse.setValues(response.getValues());

        responses.add(assessmentResponse);
      }
    }

    return responses;
  }
}
