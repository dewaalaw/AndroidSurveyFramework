package com.example.jaf50.survey;

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

    ParseObject testObject = new ParseObject("TestObject");
    testObject.put("haha", "LAWL!");
    testObject.saveInBackground();
  }
}
