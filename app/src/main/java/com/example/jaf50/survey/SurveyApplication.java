package com.example.jaf50.survey;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.response.TimeResponse;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseObject;

public class SurveyApplication extends SugarApp {

  private static final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
  private static final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";

  @Override
  public void onCreate() {
    super.onCreate();
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

    ParseObject.registerSubclass(Participant.class);
    ParseObject.registerSubclass(Survey.class);
    ParseObject.registerSubclass(Assessment.class);
    ParseObject.registerSubclass(AssessmentResponse.class);
    ParseObject.registerSubclass(TimeResponse.class);
  }
}
