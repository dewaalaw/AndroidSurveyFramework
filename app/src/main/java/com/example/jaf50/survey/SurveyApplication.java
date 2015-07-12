package com.example.jaf50.survey;

import android.app.Application;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.util.LogUtils;
import com.parse.Parse;
import com.parse.ParseObject;

import io.pristine.sheath.Sheath;

public class SurveyApplication extends Application {

  private static final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
  private static final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";

  @Override
  public void onCreate() {
    super.onCreate();
    Sheath.holster(new SurveyModule());

    LogUtils.d(getClass(), "In onCreate()");

    Parse.enableLocalDatastore(this);
    registerParseClasses();
    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
  }

  public static void registerParseClasses() {
    ParseObject.registerSubclass(Assessment.class);
    ParseObject.registerSubclass(AssessmentResponse.class);
    ParseObject.registerSubclass(Participant.class);
  }
}
