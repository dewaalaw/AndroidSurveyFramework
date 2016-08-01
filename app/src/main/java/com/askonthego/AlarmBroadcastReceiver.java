package com.askonthego;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.askonthego.alarm.LaunchSurveyTask;
import com.askonthego.alarm.SurveySchedulerManager;
import com.askonthego.util.LogUtils;

public class AlarmBroadcastReceiver extends BroadcastReceiver {

    private static final String SURVEY_NAME_KEY = "com.askonthego.alarm.qa";

    @Override
    public void onReceive(Context context, Intent intent) {
        String cronExpression = intent.getStringExtra("cron");
        String surveyName = intent.hasExtra("surveyName") ? intent.getStringExtra("surveyName") : "Beeped";
        boolean runNow = intent.getBooleanExtra("runNow", false);
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SURVEY_NAME_KEY, surveyName).commit();

        MyTask task = new MyTask();
        if (runNow) {
            SurveySchedulerManager.getInstance().saveTask(context, cronExpression, task.getClass());
            SurveySchedulerManager.getInstance().runNow(context, task.getClass(), 0);
        } else {
            SurveySchedulerManager.getInstance().saveRandomnessMillis(context, task.getClass(), 0);
            SurveySchedulerManager.getInstance().saveTask(context, cronExpression, task.getClass());
            SurveySchedulerManager.getInstance().restart(context, task.getClass());
        }

        LogUtils.d(getClass(), "In onReceive(), surveyName = " + surveyName + ", cron = " + cronExpression + ", runNow = " + runNow);
    }

    public static class MyTask extends LaunchSurveyTask {
        @Override
        protected String getSurveyName(Context context) {
            return PreferenceManager.getDefaultSharedPreferences(context).getString(SURVEY_NAME_KEY, null);
        }
    }
}
