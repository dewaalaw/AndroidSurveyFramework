package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.RegisterActivity;
import com.example.jaf50.survey.util.LogUtils;

import java.util.HashMap;

public abstract class LaunchSurveyTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      LogUtils.d(getClass(), "In doWork(), surveyName to launch = " + getSurveyName(contextWrapper));

      WakeLocker.acquire(contextWrapper);

      Intent surveyIntent = new Intent(contextWrapper, RegisterActivity.class)
          .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
          .putExtra("surveyName", getSurveyName(contextWrapper))
          .putExtra("isAlarm", true);
      contextWrapper.startActivity(surveyIntent);
    } catch (Exception e) {
      LogUtils.d(getClass(), "In doWork(), exception occurred: " + e);
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

  /*
   * Need to declare a fixed amount of non-anonymous Task classes due to limitations in the BuzzBox Scheduling Api.
   */
  public static class Task01 extends LaunchSurveyTask {}
  public static class Task02 extends LaunchSurveyTask {}
  public static class Task03 extends LaunchSurveyTask {}
  public static class Task04 extends LaunchSurveyTask {}
  public static class Task05 extends LaunchSurveyTask {}
  public static class Task06 extends LaunchSurveyTask {}
  public static class Task07 extends LaunchSurveyTask {}
  public static class Task08 extends LaunchSurveyTask {}
  public static class Task09 extends LaunchSurveyTask {}
  public static class Task10 extends LaunchSurveyTask {}
  public static class Task11 extends LaunchSurveyTask {}
  public static class Task12 extends LaunchSurveyTask {}
  public static class Task13 extends LaunchSurveyTask {}
  public static class Task14 extends LaunchSurveyTask {}
  public static class Task15 extends LaunchSurveyTask {}
  public static class Task16 extends LaunchSurveyTask {}
  public static class Task17 extends LaunchSurveyTask {}
  public static class Task18 extends LaunchSurveyTask {}
  public static class Task19 extends LaunchSurveyTask {}
  public static class Task20 extends LaunchSurveyTask {}
  public static class Task21 extends LaunchSurveyTask {}
  public static class Task22 extends LaunchSurveyTask {}
  public static class Task23 extends LaunchSurveyTask {}
  public static class Task24 extends LaunchSurveyTask {}
  public static class Task25 extends LaunchSurveyTask {}

  private static HashMap<Integer, Class<? extends LaunchSurveyTask>> taskMap = new HashMap<>();

  static {
    taskMap.put(1, Task01.class);
    taskMap.put(2, Task02.class);
    taskMap.put(3, Task03.class);
    taskMap.put(4, Task04.class);
    taskMap.put(5, Task05.class);
    taskMap.put(6, Task06.class);
    taskMap.put(7, Task07.class);
    taskMap.put(8, Task08.class);
    taskMap.put(9, Task09.class);
    taskMap.put(10, Task10.class);
    taskMap.put(11, Task11.class);
    taskMap.put(12, Task12.class);
    taskMap.put(13, Task13.class);
    taskMap.put(14, Task14.class);
    taskMap.put(15, Task15.class);
    taskMap.put(16, Task16.class);
    taskMap.put(17, Task17.class);
    taskMap.put(18, Task18.class);
    taskMap.put(19, Task19.class);
    taskMap.put(20, Task20.class);
    taskMap.put(21, Task21.class);
    taskMap.put(22, Task22.class);
    taskMap.put(23, Task23.class);
    taskMap.put(24, Task24.class);
    taskMap.put(25, Task25.class);
  }

  public static Class<? extends LaunchSurveyTask> getTask(int taskNumber) {
    return taskMap.get(taskNumber);
  }

  public static int getMaxAlarmCount() {
    return taskMap.size();
  }
}