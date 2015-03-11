package com.example.jaf50.survey.service;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.response.Response;
import com.example.jaf50.survey.ui.ISurveyComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ResponseCollectorService {

  public List<AssessmentResponse> collectResponses(Assessment assessment, List<ISurveyComponent> surveyComponents) {
    List<AssessmentResponse> responses = new ArrayList<>();
    Date responseDate = new Date();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        AssessmentResponse assessmentResponse = new AssessmentResponse();
        Response response = surveyComponent.getResponse();
        assessmentResponse.setResponseDate(responseDate);
        assessmentResponse.setAssessment(assessment);
        assessmentResponse.setResponseId(response.getId());

        List <Value> values = new ArrayList<>();
        for (Object rawValue : response.getValues()) {
          if (rawValue != null) {
            Value value = new Value();
            value.setResponse(assessmentResponse);
            value.setValue(rawValue.toString());
            values.add(value);
          }
        }
        assessmentResponse.setValues(values);

        responses.add(assessmentResponse);
      }
    }

    return responses;
  }
}
