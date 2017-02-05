package com.askonthego;

import android.app.Application;

import com.askonthego.alarm.SurveyJobCreator;
import com.evernote.android.job.JobManager;
import com.facebook.stetho.Stetho;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

import io.pristine.sheath.Sheath;

public class SurveyApplication extends Application {

    public static final String TIMEOUT_EVENT_KEY = "timeoutEvent";
    public static final String ALARM_EVENT_KEY = "alarmEvent";
    public static final String SURVEY_NAME_KEY = "surveyName";
    public static final String TIMEOUT_JOB_KEY = "timeout";

    @Override
    public void onCreate() {
        super.onCreate();
        Sheath.holster(new SurveyModule(this));
        JobManager.create(this).addJobCreator(new SurveyJobCreator());

        if (BuildConfig.DEBUG) {
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(new CouchbaseInspectorModulesProvider.Builder(this).showMetadata(true).build())
                .build());
        }
    }
}
