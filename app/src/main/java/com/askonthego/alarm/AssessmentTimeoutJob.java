package com.askonthego.alarm;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.askonthego.AssessmentHolder;
import com.askonthego.LoginActivity;
import com.askonthego.SurveyApplication;
import com.askonthego.service.ResourceService;
import com.evernote.android.job.Job;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import bolts.Task;
import io.pristine.sheath.Sheath;

public class AssessmentTimeoutJob extends Job {

    @Inject AssessmentHolder assessmentHolder;
    @Inject SurveyAlarmScheduler surveyAlarmScheduler;
    @Inject ResourceService resourceService;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        try {
            Sheath.inject(this);

            Log.d(getClass().getName(), "In onRunJob()...");
            if (assessmentHolder.isAssessmentInProgress()) {
                Log.d(getClass().getName(), "In onRunJob(), assessmentInProgress block.");
                Intent intent = new Intent(
                    getContext().getApplicationContext(),
                    LoginActivity.class
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                if (params.getExtras() != null) {
                    String surveyName = (String) params.getExtras().get(SurveyApplication.SURVEY_NAME_KEY);
                    Log.d(getClass().getName(), "On onRunJob() for " + getClass().getSimpleName() + ", surveyName = " + surveyName);
                    intent.putExtra(SurveyApplication.SURVEY_NAME_KEY, surveyName);
                    intent.putExtra(SurveyApplication.TIMEOUT_EVENT_KEY, new TimeoutEvent(surveyName));
                }

                getContext().getApplicationContext().startActivity(intent);
            } else {
                // Reschedule the alarms to ensure that the next alarm runs. This is important in case there is exactly
                // one alarm expression in the alarm schedule list, since the only other place that rescheduling happens is
                // when the app starts up.
                scheduleAlarms(getContext().getApplicationContext());
                Log.d(getClass().getName(), "No assessment is currently in progress so the timeout task is not notifying the SurveyActivity.");
            }
        } catch (Exception e) {
            Log.d(getClass().getName(), "In onRunJob(), exception occurred: " + e);
        }
        return null;
    }

    private void scheduleAlarms(final Context context) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                surveyAlarmScheduler.scheduleAll(resourceService.getAlarmInputStream(context));
                return null;
            }
        });
    }
}
