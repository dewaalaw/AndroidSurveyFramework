package com.askonthego.alarm;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.askonthego.LoginActivity;
import com.askonthego.SurveyApplication;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;

import javax.inject.Inject;

import io.pristine.sheath.Sheath;

public class LaunchSurveyJob extends Job {

    @Inject WakeLocker wakeLocker;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Sheath.inject(this);
        wakeLocker.acquireFull(getContext());

        if (params.getExtras() != null) {
            String surveyName = (String) params.getExtras().get(SurveyApplication.SURVEY_NAME_KEY);
            if (surveyName != null) {
                try {
                    // Cancel existing timeout alarm just in case the user is currently in a survey. We
                    // don't want the existing timeout to conflict with the alarmed survey timeout.
                    JobManager.instance().cancelAllForTag(SurveyApplication.TIMEOUT_JOB_KEY);

                    Log.d(getClass().getName(), "In onRunJob(), surveyName to launch = " + surveyName);

                    Intent surveyIntent = new Intent(getContext(), LoginActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra(SurveyApplication.SURVEY_NAME_KEY, surveyName)
                            .putExtra(SurveyApplication.ALARM_EVENT_KEY, new AlarmEvent(surveyName));
                    getContext().startActivity(surveyIntent);
                } catch (Exception e) {
                    Log.d(getClass().getName(), "In onRunJob(), exception occurred: " + e);
                }
            }
        }
        return Result.SUCCESS;
    }
}
