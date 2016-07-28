package com.askonthego.alarm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.askonthego.parser.alarm.AlarmModel;
import com.askonthego.parser.alarm.ScheduleModel;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SurveyAlarmScheduler {

  private Gson gson;

  public SurveyAlarmScheduler(Gson gson) {
    this.gson = gson;
  }

  public void scheduleAll(Context context, InputStream alarmInputStream) {
    ScheduleModel scheduleModel = gson.fromJson(new InputStreamReader(alarmInputStream), ScheduleModel.class);
    if (scheduleModel.getAlarms().size() > LaunchSurveyTask.getMaxAlarmCount()) {
      throw new IllegalStateException("Max available alarms exceeded. Up to " + LaunchSurveyTask.getMaxAlarmCount() + " alarms are allowed but " +
                                      scheduleModel.getAlarms().size() + " are specified.");
    }

    SurveySchedulerManager.getInstance().stopAll(context);

    int taskNumber = 1;
    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
    for (AlarmModel alarmModel : scheduleModel.getAlarms()) {
      Class<? extends LaunchSurveyTask> taskClass = LaunchSurveyTask.getTask(taskNumber++);
      editor.putString(taskClass.getSimpleName(), alarmModel.getSurveyName());

      if (alarmModel.getMinuteRandomness() > 0) {
        SurveySchedulerManager.getInstance().saveRandomnessMillis(context, taskClass, alarmModel.getMinuteRandomness() * 60 * 1000);
      }
      SurveySchedulerManager.getInstance().saveTask(context, alarmModel.getScheduleExpression(), taskClass);
      SurveySchedulerManager.getInstance().restart(context, taskClass);

      Log.d(getClass().getName(), "Scheduled alarm " + alarmModel.getScheduleExpression() + " for survey " + alarmModel.getSurveyName());
    }
    editor.commit();
  }
}
