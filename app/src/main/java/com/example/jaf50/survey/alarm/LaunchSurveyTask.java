package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;
import android.content.Intent;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.RegisterActivity;

public class LaunchSurveyTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      Intent surveyIntent = new Intent(contextWrapper, RegisterActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
      // TODO - replace this value with the actual survey name to launch.
      surveyIntent.putExtra("surveyName", "Beeped");
      contextWrapper.startActivity(surveyIntent);
    } catch (Exception e) {
    }

    return new TaskResult();
  }

  @Override
  public String getTitle() {
    return "Survey";
  }

  @Override
  public String getId() {
    return "LaunchSurvey";
  }
}
