package com.askonthego;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

import io.pristine.sheath.Sheath;

public class SurveyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Sheath.holster(new SurveyModule(this));

        if (BuildConfig.DEBUG) {
            Stetho.initialize(Stetho.newInitializerBuilder(this)
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .enableWebKitInspector(new CouchbaseInspectorModulesProvider.Builder(this).showMetadata(true).build())
                .build());
        }
    }
}
