package com.askonthego;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.domain.Participant;
import com.askonthego.util.LogUtils;
import com.parse.Parse;
import com.parse.ParseObject;

import io.fabric.sdk.android.Fabric;
import io.pristine.sheath.Sheath;

public class SurveyApplication extends Application {

  private static final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
  private static final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";

  @Override
  public void onCreate() {
    super.onCreate();
    Fabric.with(this, new Crashlytics());
    Sheath.holster(new SurveyModule(this));

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
