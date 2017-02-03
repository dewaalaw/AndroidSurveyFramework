package com.askonthego.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import com.askonthego.util.LogUtils;
import com.buzzbox.mob.android.scheduler.Logger;
import com.buzzbox.mob.android.scheduler.ScheduledTask;
import com.buzzbox.mob.android.scheduler.SchedulerReceiver;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.analytics.AnalyticsManager;
import com.buzzbox.mob.android.scheduler.cron.Predictor;
import com.buzzbox.mob.android.scheduler.cron.SchedulingPattern;
import com.buzzbox.mob.android.scheduler.db.MigrableSQLiteHelper;
import com.buzzbox.mob.android.scheduler.ui.MetaDataUtils;
import com.buzzbox.mob.android.scheduler.ui.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SurveySchedulerManager {

    private static final String TAG = SurveySchedulerManager.class.getName();
    private static Random random = new Random();
    private static final String STATUS_DISABLED = "DISABLED";
    private static final String STATUS_PAUSED = "PAUSED";
    private static final String TASKS_PREFERENCE_KEY = "buzzbox.scheduler.tasks";
    private static final String RANDOMNESS_PREFERENCE_KEY = "buzzbox.scheduler.randomness";
    private static final String TASKS_STATUS_PREFERENCE_KEY_PREFIX = "buzzbox.scheduler.tasks.status.";
    public static final int SCHEDULER_CONFIG_REQ_CODE = 919817235;
    private static SurveySchedulerManager instance = null;

    private SurveySchedulerManager() {
    }

    public static SurveySchedulerManager getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (SurveySchedulerManager.class) {
            if (instance == null) {
                instance = new SurveySchedulerManager();
            }
        }
        return instance;
    }

    public boolean isRunning(Context context) {
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.enabled) {
                return true;
            }
        }
        return false;
    }

    public void saveTask(Context context, String cron, Class<? extends Task> taskClass) {
        saveTask(context, cron, taskClass, false, 0);
    }

    private void saveNotificationTypesDefault(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean defaultAlreadySaved = prefs.getBoolean("manifest.default.already.saved", false);
        if (defaultAlreadySaved) {
            return;
        }
        String notificationTypesString = MetaDataUtils.getString(context, "SchedulerPreferenceActivity.notificationTypes", null);
        if ((notificationTypesString != null) && (!"".equals(notificationTypesString.trim()))) {
            String[] notificationTypes = notificationTypesString.split(",");
            SharedPreferences.Editor editPrefs = prefs.edit();
            editPrefs.putBoolean("manifest.default.already.saved", true);
            for (int i = 0; i < notificationTypes.length; i++) {
                String defaultSettingsString = MetaDataUtils.getString(context, "SchedulerPreferenceActivity.notificationTypes.defaultSettings." + notificationTypes[i], "statusBar=enabled,vibrate=disabled,led=disabled,sound=disabled");
                Map<String, String> defaultSettingsMap = StringUtils.optionsInteger2map(",", '=', defaultSettingsString);

                editPrefs.putBoolean("cron.led." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("led")));
                editPrefs.putBoolean("cron.sound." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("sound")));
                editPrefs.putBoolean("cron.vibrate." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("vibrate")));
                editPrefs.putBoolean("cron.statusBar." + notificationTypes[i], "enabled".equals(defaultSettingsMap.get("statusBar")));
            }
            editPrefs.commit();
        }
    }

    public void saveTask(Context context, String cron, Class<? extends Task> taskClass, boolean deliverAsapOnDelay, int autoPauseHours) {
        String taskClassName = taskClass.getName();

        MigrableSQLiteHelper.createFirstTime(context);

        saveNotificationTypesDefault(context);

        boolean manifestRequirementsOk = true;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();

        boolean permissionsChecked = prefs.getBoolean("manifest.permissions.checked", false);
        if (!permissionsChecked) {
            edit.putBoolean("manifest.permissions.checked", true);
            if (!MetaDataUtils.checkSchedulerPermission(context)) {
                manifestRequirementsOk = false;
                Log.e(TAG, "<task not saved!>");
            }
        }
        boolean servicesChecked = prefs.getBoolean("manifest.services.checked", false);
        if (!servicesChecked) {
            edit.putBoolean("manifest.services.checked", true);
            if (!MetaDataUtils.checkSchedulerServices(context)) {
                manifestRequirementsOk = false;
                Log.e(TAG, "<task not saved!>");
            }
        }
        boolean receiversChecked = prefs.getBoolean("manifest.receivers.checked", false);
        if (!receiversChecked) {
            edit.putBoolean("manifest.receivers.checked", true);
            if (!MetaDataUtils.checkSchedulerReceivers(context)) {
                manifestRequirementsOk = false;
                Log.e(TAG, "<task not saved!>");
            }
        }
        edit.commit();
        if (!manifestRequirementsOk) {
            return;
        }
        List<ScheduledTask> tasks = loadTasks(context);

        new SchedulingPattern(cron);

        StringBuilder serialized = new StringBuilder();
        for (ScheduledTask savedTask : tasks) {
            if (!savedTask.taskClassName.equals(taskClassName)) {
                serialized.append(savedTask.taskClassName);
                serialized.append("@");
                serialized.append(savedTask.cron);
                serialized.append("@");
                serialized.append(savedTask.deliverAsapOnDelay ? "1" : "0");
                serialized.append("@");
                serialized.append(savedTask.getAutoPauseHours());
                serialized.append(";");
            }
        }
        serialized.append(taskClassName);
        serialized.append("@");
        serialized.append(cron);
        serialized.append("@");
        serialized.append(deliverAsapOnDelay ? "1" : "0");
        serialized.append("@");
        serialized.append(autoPauseHours);

        Log.d(TAG, serialized.toString());
        edit.putString("buzzbox.scheduler.tasks", serialized.toString());
        edit.commit();
    }

    protected ScheduledTask loadTask(Context context, String taskClassName) {
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.taskClassName.equals(taskClassName)) {
                return saved;
            }
        }
        Log.e(TAG, "Class " + taskClassName + " has never been saved. Use SchdulerManager.getInstance().saveTask(...)");
        return null;
    }

    protected List<ScheduledTask> loadTasks(Context context) {
        List<ScheduledTask> tasks = new ArrayList();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String serialized = prefs.getString("buzzbox.scheduler.tasks", "");
        if (serialized.length() == 0) {
            return tasks;
        }
        for (String oneTask : serialized.split(";")) {
            try {
                String[] elements = oneTask.split("@");
                boolean needsNetwork = "1".equals(elements[2]);
                ScheduledTask saved = new ScheduledTask(elements[0], elements[1], needsNetwork);
                if (elements.length >= 4) {
                    int h = Integer.parseInt(elements[3]);
                    saved.setAutoPauseHours(h);
                }
                String status = getStatus(context, elements[0]);
                if (status.startsWith(STATUS_DISABLED)) {
                    saved.setEnabled(false);
                }
                if (status.equals(STATUS_PAUSED)) {
                    saved.paused = true;
                }
                tasks.add(saved);
            } catch (Exception e) {
                Log.e(TAG, "can't parse task [" + oneTask + "]");
            }
        }
        return tasks;
    }

    public void runNow(Context ctx, Class<? extends Task> taskClass, long inMillisDelay) {
        ScheduledTask task = loadTask(ctx, taskClass.getName());
        if (task != null) {
            scheduleRetry(ctx, taskClass.getName(), task.cron, inMillisDelay, 0);
        }
    }

    public void runAllNow(Context ctx, long inMillisDelay) {
        for (ScheduledTask scheduledTask : loadTasks(ctx)) {
            scheduleRetry(ctx, scheduledTask.taskClassName, scheduledTask.cron, inMillisDelay, 0);
        }
    }

    private void scheduleAlarm(int type, long triggerAtMillis, PendingIntent pendingIntent, AlarmManager alarmManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // setAlarmClock will wake the device up even if it is in an idle state.
            alarmManager.setAlarmClock(new AlarmManager.AlarmClockInfo(triggerAtMillis, pendingIntent), pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(type, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(type, triggerAtMillis, pendingIntent);
        }
    }

    protected void scheduleRetry(Context context, String className, String cron, long inMillisDelay, int retryCount) {
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent i = new Intent(context, SchedulerReceiver.class);
        i.setAction(className);
        i.putExtra("taskClass", className);
        i.putExtra("cron", cron);
        i.putExtra("retryCount", retryCount);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        long now = System.currentTimeMillis();
        scheduleAlarm(AlarmManager.RTC_WAKEUP, now + inMillisDelay, pi, mgr);

        try {
            Class<?> taskClass = Class.forName(className);
            String taskId = ((Task) taskClass.newInstance()).getId();
            Logger.log(context, taskId + "-scheduled-" + inMillisDelay / 1000L, now, now, 0, retryCount);
        } catch (Exception e) {
            Logger.log(context, e.getMessage(), now, now, 0, retryCount);
        }
        Log.i(TAG, "Alarm Set for Retry Task [" + className + "] in [" + inMillisDelay / 60000L + "] minutes");
    }

    public void restart(Context context, Class<?> taskClass) {
        String className = taskClass.getName();
        for (ScheduledTask saved : loadTasks(context)) {
            if (saved.taskClassName.equals(className)) {
                restartSavedTask(context, taskClass, saved);
                break;
            }
        }
    }

    private void restartSavedTask(Context context, Class<?> taskClass, ScheduledTask saved) {
        setStatus(context, taskClass.getName(), "OK", System.currentTimeMillis());
        saved.paused = false;
        saved.enabled = true;
        scheduleNext(context, saved, 0L);
        try {
            AnalyticsManager.setUserParam(PreferenceManager.getDefaultSharedPreferences(context), "task-" + ((Task) taskClass.newInstance()).getId(), "on");
        } catch (Exception localException) {
        }
    }

    public void stop(Context context, Class<?> taskClass) {
        String taskClassName = taskClass.getName();
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, SchedulerReceiver.class);
        i.setAction(taskClassName);
        i.putExtra("taskClass", taskClassName);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        mgr.cancel(pi);
        setStatus(context, taskClassName, STATUS_DISABLED, System.currentTimeMillis());
        Log.i(TAG, "Canceled Alarm for Task [" + taskClassName + "]");
        long now = System.currentTimeMillis();
        try {
            String taskId = ((Task) taskClass.newInstance()).getId();
            Logger.log(context, taskId + "-disabled", now, now, 0, 0);
            AnalyticsManager.setUserParam(PreferenceManager.getDefaultSharedPreferences(context), "task-" + taskId, "off");
        } catch (Exception localException) {
        }
    }

    protected void scheduleNext(Context context, ScheduledTask task, long delay) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        long lastOpen = prefs.getLong("buzzbox.analytics.sessionts", 0L);
        long now = System.currentTimeMillis();
        long lastOpenDiff = now - lastOpen;
        if ((task.paused) || (!task.enabled)) {
            Logger.log(context, task.taskClassName + " SKIP schedule. paused: " + task.paused + " enabled: " + task.enabled, now, now, 0, 0);
            return;
        }
        if ((task.getAutoPauseHours() > 0) && (lastOpen > 0L) && (lastOpenDiff > task.getAutoPauseHours() * 3600000L)) {
            try {
                Class<?> taskClass = Class.forName(task.taskClassName);
                String taskId = ((Task) taskClass.newInstance()).getId();
                Logger.log(context, taskId + "-paused", now, now, 0, 0);
                setStatus(context, task.taskClassName, STATUS_PAUSED, now);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.log(context, e.getMessage(), now, now, 0, 0);
            }
            return;
        }
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Predictor predictor = new Predictor(task.cron);

        Intent i = new Intent(context, SchedulerReceiver.class);
        i.setAction(task.taskClassName);
        i.putExtra("taskClass", task.taskClassName);
        i.putExtra("cron", task.cron);

        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);

        long matchingTime = predictor.nextMatchingTime() + delay;

        int randomness = prefs.getInt(getRandomnessKey(task.taskClassName), 0);
        if ((randomness > 0) && (matchingTime - now > randomness)) {
            int addingMillis = random.nextInt(randomness);
            matchingTime += addingMillis;
            Log.i(getClass().getName(), "Added randomness millis " + addingMillis + " to schedule " + task.cron);
        }

        scheduleAlarm(AlarmManager.RTC_WAKEUP, matchingTime, pi, mgr);

        LogUtils.d(getClass(),
            "Alarm Set for Task [" + task.taskClassName + "] with Schedule [" + task.cron + "] at date " + new Date(matchingTime));
    }

    protected String getStatus(Context context, String taskClass) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString("buzzbox.scheduler.tasks.status." + taskClass, "UNK");
    }

    protected void setStatus(Context context, String taskClass, String status, long ts) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString("buzzbox.scheduler.tasks.status." + taskClass, status + "@" + ts);
        edit.commit();
    }

    public boolean restartAll(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            try {
                if (savedTask.isEnabled()) {
                    restartSavedTask(context, Class.forName(savedTask.taskClassName), savedTask);
                }
            } catch (ClassNotFoundException localClassNotFoundException) {
            }
        }
        return true;
    }

    public boolean restartPaused(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            if (savedTask.paused) {
                setStatus(context, savedTask.taskClassName, "OK", System.currentTimeMillis());
                savedTask.paused = false;
                scheduleNext(context, savedTask, 0L);
            }
        }
        return true;
    }

    public boolean stopAll(Context context) {
        for (ScheduledTask savedTask : loadTasks(context)) {
            try {
                stop(context, Class.forName(savedTask.taskClassName));
            } catch (ClassNotFoundException localClassNotFoundException) {
            }
        }
        return true;
    }

    public void saveGlobalRandomness(Context context, int millis) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt("buzzbox.scheduler.randomness", millis);
        edit.commit();
    }

    public void saveRandomnessMillis(Context context, Class<? extends Task> taskClass, int randomnessMillis) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putInt(getRandomnessKey(taskClass.getName()), randomnessMillis)
            .commit();
    }

    private String getRandomnessKey(String taskClassName) {
        return "buzzbox.scheduler.randomness." + taskClassName;
    }
}
