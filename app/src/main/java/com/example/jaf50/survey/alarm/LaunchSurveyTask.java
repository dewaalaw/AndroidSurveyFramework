package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;

import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.RegisterActivity;

public class LaunchSurveyTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    TaskResult result = new TaskResult();
    result.addMessage(
        new NotificationMessage(
            "Complete a Survey", "Please complete a survey")
            .setNotificationClickIntentClass(RegisterActivity.class));
    return result;
  }

  @Override
  public String getTitle() {
    return "Beep!";
  }

  @Override
  public String getId() {
    return "Beep";
  }
}
