package com.askonthego;

import android.app.Application;

import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.domain.Participant;
import com.facebook.stetho.Stetho;
import com.parse.Parse;
import com.parse.ParseObject;
import com.robotpajamas.stetho.couchbase.CouchbaseInspectorModulesProvider;

import io.pristine.sheath.Sheath;

public class SurveyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Sheath.holster(new SurveyModule(this));

        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                    .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                    .enableWebKitInspector(new CouchbaseInspectorModulesProvider.Builder(this)
                            .showMetadata(true)
                            .build())
                    .build());
    }
}
