package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.RegisterActivity;

public abstract class LaunchSurveyTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      Log.d(getClass().getName(), "In doWork(), surveyName to launch = " + getSurveyName(contextWrapper));

      Intent surveyIntent = new Intent(contextWrapper, RegisterActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra("surveyName", getSurveyName(contextWrapper));
      contextWrapper.startActivity(surveyIntent);
    } catch (Exception e) {
      Log.d(getClass().getName(), "In doWork(), exception occurred: " + e);
    }

    return new TaskResult();
  }

  @Override
  public String getTitle() {
    return "Survey";
  }

  public String getId() {
    return getClass().getSimpleName();
  }

  private String getSurveyName(ContextWrapper context) {
    return PreferenceManager.getDefaultSharedPreferences(context)
                            .getString(getClass().getSimpleName(), null);
  }

  public static class Task1 extends LaunchSurveyTask {}
  public static class Task2 extends LaunchSurveyTask {}
  public static class Task3 extends LaunchSurveyTask {}
  public static class Task4 extends LaunchSurveyTask {}
  public static class Task5 extends LaunchSurveyTask {}
}