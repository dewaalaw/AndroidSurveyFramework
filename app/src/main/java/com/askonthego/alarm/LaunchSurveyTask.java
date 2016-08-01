package com.askonthego.alarm;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.preference.PreferenceManager;

import com.askonthego.RegisterActivity;
import com.askonthego.util.LogUtils;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

import java.util.HashMap;

import de.greenrobot.event.EventBus;

public abstract class LaunchSurveyTask implements Task {

    @Override
    public TaskResult doWork(ContextWrapper contextWrapper) {
        try {
            LogUtils.d(getClass(), "In doWork(), surveyName to launch = " + getSurveyName(contextWrapper));

            Intent surveyIntent = new Intent(contextWrapper, RegisterActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
                .putExtra("surveyName", getSurveyName(contextWrapper));
            EventBus.getDefault().postSticky(new AlarmEvent(getSurveyName(contextWrapper)));
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

    protected String getSurveyName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(getClass().getSimpleName(), null);
    }

    /*
     * Need to declare a fixed amount of non-anonymous Task classes due to limitations in the BuzzBox Scheduling Api.
     */
    public static class SurveyTask01 extends LaunchSurveyTask {
    }

    public static class SurveyTask02 extends LaunchSurveyTask {
    }

    public static class SurveyTask03 extends LaunchSurveyTask {
    }

    public static class SurveyTask04 extends LaunchSurveyTask {
    }

    public static class SurveyTask05 extends LaunchSurveyTask {
    }

    public static class SurveyTask06 extends LaunchSurveyTask {
    }

    public static class SurveyTask07 extends LaunchSurveyTask {
    }

    public static class SurveyTask08 extends LaunchSurveyTask {
    }

    public static class SurveyTask09 extends LaunchSurveyTask {
    }

    public static class SurveyTask10 extends LaunchSurveyTask {
    }

    public static class SurveyTask11 extends LaunchSurveyTask {
    }

    public static class SurveyTask12 extends LaunchSurveyTask {
    }

    public static class SurveyTask13 extends LaunchSurveyTask {
    }

    public static class SurveyTask14 extends LaunchSurveyTask {
    }

    public static class SurveyTask15 extends LaunchSurveyTask {
    }

    public static class SurveyTask16 extends LaunchSurveyTask {
    }

    public static class SurveyTask17 extends LaunchSurveyTask {
    }

    public static class SurveyTask18 extends LaunchSurveyTask {
    }

    public static class SurveyTask19 extends LaunchSurveyTask {
    }

    public static class SurveyTask20 extends LaunchSurveyTask {
    }

    public static class SurveyTask21 extends LaunchSurveyTask {
    }

    public static class SurveyTask22 extends LaunchSurveyTask {
    }

    public static class SurveyTask23 extends LaunchSurveyTask {
    }

    public static class SurveyTask24 extends LaunchSurveyTask {
    }

    public static class SurveyTask25 extends LaunchSurveyTask {
    }

    public static class SurveyTask26 extends LaunchSurveyTask {
    }

    public static class SurveyTask27 extends LaunchSurveyTask {
    }

    public static class SurveyTask28 extends LaunchSurveyTask {
    }

    public static class SurveyTask29 extends LaunchSurveyTask {
    }

    public static class SurveyTask30 extends LaunchSurveyTask {
    }

    public static class SurveyTask31 extends LaunchSurveyTask {
    }

    public static class SurveyTask32 extends LaunchSurveyTask {
    }

    public static class SurveyTask33 extends LaunchSurveyTask {
    }

    public static class SurveyTask34 extends LaunchSurveyTask {
    }

    public static class SurveyTask35 extends LaunchSurveyTask {
    }

    public static class SurveyTask36 extends LaunchSurveyTask {
    }

    public static class SurveyTask37 extends LaunchSurveyTask {
    }

    public static class SurveyTask38 extends LaunchSurveyTask {
    }

    public static class SurveyTask39 extends LaunchSurveyTask {
    }

    public static class SurveyTask40 extends LaunchSurveyTask {
    }

    public static class SurveyTask41 extends LaunchSurveyTask {
    }

    public static class SurveyTask42 extends LaunchSurveyTask {
    }

    public static class SurveyTask43 extends LaunchSurveyTask {
    }

    public static class SurveyTask44 extends LaunchSurveyTask {
    }

    public static class SurveyTask45 extends LaunchSurveyTask {
    }

    private static HashMap<Integer, Class<? extends LaunchSurveyTask>> taskMap = new HashMap<>();

    static {
        taskMap.put(1, SurveyTask01.class);
        taskMap.put(2, SurveyTask02.class);
        taskMap.put(3, SurveyTask03.class);
        taskMap.put(4, SurveyTask04.class);
        taskMap.put(5, SurveyTask05.class);
        taskMap.put(6, SurveyTask06.class);
        taskMap.put(7, SurveyTask07.class);
        taskMap.put(8, SurveyTask08.class);
        taskMap.put(9, SurveyTask09.class);
        taskMap.put(10, SurveyTask10.class);
        taskMap.put(11, SurveyTask11.class);
        taskMap.put(12, SurveyTask12.class);
        taskMap.put(13, SurveyTask13.class);
        taskMap.put(14, SurveyTask14.class);
        taskMap.put(15, SurveyTask15.class);
        taskMap.put(16, SurveyTask16.class);
        taskMap.put(17, SurveyTask17.class);
        taskMap.put(18, SurveyTask18.class);
        taskMap.put(19, SurveyTask19.class);
        taskMap.put(20, SurveyTask20.class);
        taskMap.put(21, SurveyTask21.class);
        taskMap.put(22, SurveyTask22.class);
        taskMap.put(23, SurveyTask23.class);
        taskMap.put(24, SurveyTask24.class);
        taskMap.put(25, SurveyTask25.class);
        taskMap.put(26, SurveyTask26.class);
        taskMap.put(27, SurveyTask27.class);
        taskMap.put(28, SurveyTask28.class);
        taskMap.put(29, SurveyTask29.class);
        taskMap.put(30, SurveyTask30.class);
        taskMap.put(31, SurveyTask31.class);
        taskMap.put(32, SurveyTask32.class);
        taskMap.put(33, SurveyTask33.class);
        taskMap.put(34, SurveyTask34.class);
        taskMap.put(35, SurveyTask35.class);
        taskMap.put(36, SurveyTask36.class);
        taskMap.put(37, SurveyTask37.class);
        taskMap.put(38, SurveyTask38.class);
        taskMap.put(39, SurveyTask39.class);
        taskMap.put(40, SurveyTask40.class);
        taskMap.put(41, SurveyTask41.class);
        taskMap.put(42, SurveyTask42.class);
        taskMap.put(43, SurveyTask43.class);
        taskMap.put(44, SurveyTask44.class);
        taskMap.put(45, SurveyTask45.class);
    }

    public static Class<? extends LaunchSurveyTask> getTask(int taskNumber) {
        return taskMap.get(taskNumber);
    }

    public static int getMaxAlarmCount() {
        return taskMap.size();
    }
}