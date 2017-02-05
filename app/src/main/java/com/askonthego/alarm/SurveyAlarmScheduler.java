package com.askonthego.alarm;

import android.util.Log;

import com.askonthego.parser.alarm.AlarmModel;
import com.askonthego.parser.alarm.ScheduleModel;
import com.buzzbox.mob.android.scheduler.cron.Predictor;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;
import com.evernote.android.job.util.support.PersistableBundleCompat;
import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Set;

public class SurveyAlarmScheduler {

    private Gson gson;

    public SurveyAlarmScheduler(Gson gson) {
        this.gson = gson;
    }

    public void scheduleAll(InputStream alarmInputStream) {
        ScheduleModel scheduleModel = gson.fromJson(new InputStreamReader(alarmInputStream), ScheduleModel.class);

        Set<JobRequest> jobRequestSet = JobManager.instance().getAllJobRequests();
        for(JobRequest jobRequest : jobRequestSet) {
            if (!"timeout".equals(jobRequest.getTag())) {
                // Cancel all survey alarms so that redundant alarms are not scheduled.
                JobManager.instance().cancel(jobRequest.getJobId());
            }
        }

        for (AlarmModel alarmModel : scheduleModel.getAlarms()) {
            // Calculate the next survey alarm time, based on the cron expression.
            Predictor predictor = new Predictor(alarmModel.getScheduleExpression());
            long alarmTimeMillis = predictor.nextMatchingTime();
            long now = System.currentTimeMillis();
            // The job scheduler uses millisecond offsets from the current system clock time. The calculated
            // cron times are absolute millisecond times that include the current system clock time. Calculate
            // the offset time.
            alarmTimeMillis = alarmTimeMillis - now;

            PersistableBundleCompat persistableBundleCompat = new PersistableBundleCompat();
            persistableBundleCompat.putString("surveyName", alarmModel.getSurveyName());

            new JobRequest.Builder(alarmModel.getSurveyName())
                    .setExact(alarmTimeMillis)
                    .setPersisted(true)
                    .setExtras(persistableBundleCompat)
                    .build()
                    .schedule();

            Log.d(getClass().getName(), "Scheduled alarm cron " + alarmModel.getScheduleExpression() + " for survey " + alarmModel.getSurveyName() + " at " + new Date(alarmTimeMillis));
        }
    }

//    private void scheduleMockAlarms() {
//        PersistableBundleCompat persistableBundleCompat = new PersistableBundleCompat();
//        persistableBundleCompat.putString("surveyName", "Beeped");
//
//        new JobRequest.Builder("Beeped")
//                .setExact(20000)
//                .setPersisted(true)
//                .setExtras(persistableBundleCompat)
//                .build()
//                .schedule();
//    }
}
