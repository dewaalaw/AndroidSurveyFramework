package com.example.jaf50.survey.service;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class DomainSerializationService {

  public static String toJson(Assessment assessment) throws JSONException {
    JSONObject jsonObject = new JSONObject();
    jsonObject.put("surveyName", assessment.getSurveyName());
    jsonObject.put("startDate", assessment.getAssessmentStartDate());
    jsonObject.put("endDate", assessment.getAssessmentEndDate());
    jsonObject.put("timeoutDate", assessment.getAssessmentTimeoutDate());

    if (assessment.getParticipant() != null) {
      jsonObject.put("participantId", assessment.getParticipant().getUsername());
    }

    JSONArray responses = new JSONArray();
    for(AssessmentResponse response : assessment.getResponses()) {
      JSONObject responseObject = new JSONObject();
      responseObject.put("responseId", response.getResponseId());
      responseObject.put("responseDate", response.getResponseDate());

      JSONArray values = new JSONArray();
      List<Object> rawValues = response.getValues();
      if (rawValues != null) {
        for (Object value : rawValues) {
          values.put(value);
        }
      }
      responseObject.put("values", values);

      responses.put(responseObject);
    }
    jsonObject.put("responses", responses);

    return jsonObject.toString();
  }
}
