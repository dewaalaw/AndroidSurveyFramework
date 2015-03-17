package com.example.jaf50.survey;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Survey;

public class SurveyTestUtils {

  public static void deleteAllRecords() {
    if (AssessmentResponse.listAll(AssessmentResponse.class).size() > 0) {
      AssessmentResponse.deleteAll(AssessmentResponse.class);
    }
    if (Assessment.listAll(Assessment.class).size() > 0) {
      Assessment.deleteAll(Assessment.class);
    }
    if (Survey.listAll(Survey.class).size() > 0) {
      Survey.deleteAll(Survey.class);
    }
  }
}
