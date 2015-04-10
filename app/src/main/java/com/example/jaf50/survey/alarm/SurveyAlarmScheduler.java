package com.example.jaf50.survey.alarm;

import android.content.Context;
import android.preference.PreferenceManager;

import com.buzzbox.mob.android.scheduler.SchedulerManager;

public class SurveyAlarmScheduler {

  public void scheduleAll(Context context) {
    PreferenceManager.getDefaultSharedPreferences(context).edit()
        .putString(LaunchSurveyTask.Task1.class.getSimpleName(), "Beeped")
        .putString(LaunchSurveyTask.Task2.class.getSimpleName(), "Waking")
        .commit();

    SchedulerManager.getInstance().stopAll(context);
    SchedulerManager.getInstance().saveTask(context, "*/3 * * * *", LaunchSurveyTask.Task1.class);
    SchedulerManager.getInstance().saveTask(context, "* * * * *", LaunchSurveyTask.Task2.class);
    SchedulerManager.getInstance().restart(context, LaunchSurveyTask.Task1.class);
    SchedulerManager.getInstance().restart(context, LaunchSurveyTask.Task2.class);
  }
}
