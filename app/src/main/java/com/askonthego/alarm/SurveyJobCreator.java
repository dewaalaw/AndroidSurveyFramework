package com.askonthego.alarm;

import com.askonthego.SurveyApplication;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

public class SurveyJobCreator implements JobCreator {

    @Override
    public Job create(String tag) {
        switch (tag) {
            case SurveyApplication.TIMEOUT_JOB_KEY:
                return new AssessmentTimeoutJob();
            default:
                return new LaunchSurveyJob();
        }
    }
}
