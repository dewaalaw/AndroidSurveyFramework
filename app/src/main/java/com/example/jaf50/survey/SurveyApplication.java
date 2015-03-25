package com.example.jaf50.survey;

import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseObject;

import java.util.Arrays;
import java.util.Date;

public class SurveyApplication extends SugarApp {

  private static final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
  private static final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";

  @Override
  public void onCreate() {
    super.onCreate();
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

    ParseObject survey = new ParseObject("Survey");
    survey.put("name", "My Survey");

    ParseObject participant = new ParseObject("Participant");
    participant.put("assignedId", "123");

    ParseObject assessmentResponse1 = new ParseObject("AssessmentResponse");
    assessmentResponse1.put("responseId", "var1");
    assessmentResponse1.put("responseDate", new Date());
    assessmentResponse1.add("value", "val1");

    ParseObject assessmentResponse2 = new ParseObject("AssessmentResponse");
    assessmentResponse2.put("responseId", "var2");
    assessmentResponse2.put("responseDate", new Date());
    assessmentResponse2.add("value", "val2");

    ParseObject assessment = new ParseObject("Assessment");
    assessment.put("participant", participant);
    assessment.put("survey", survey);
    assessment.put("description", "Survey Description");
    assessment.put("responses", Arrays.asList(assessmentResponse1, assessmentResponse2));
    assessment.saveInBackground();
  }
}
