package com.askonthego;

import android.content.Context;

import com.askonthego.alarm.AssessmentTimeoutJob;
import com.askonthego.alarm.LaunchSurveyJob;
import com.askonthego.service.ResourceService;
import com.askonthego.alarm.SurveyAlarmScheduler;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.service.AssessmentDAO;
import com.askonthego.service.AssessmentUploader;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.RegistrationService;
import com.askonthego.service.ParticipantDAO;
import com.askonthego.service.Preferences;
import com.askonthego.service.ResponseCollector;
import com.askonthego.service.RestAssessmentService;
import com.askonthego.service.RestUserService;
import com.askonthego.service.StudyParser;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;

@Module(injects = {
    AssessmentTimeoutJob.class,
    LaunchSurveyJob.class,
    SurveyActivity.class,
    SurveyScreen.class,
    LoginActivity.class,
    RegisterActivity.class,
    WelcomeActivity.class
})
class SurveyModule {

    private static final String DB_NAME = "survey_db";
    private Context context;

    public SurveyModule(Context context) {
        this.context = context;
    }

    @Provides
    public Database getDatabase() {
        try {
            Manager manager = new Manager(new AndroidContext(this.context), Manager.DEFAULT_OPTIONS);
            return manager.getDatabase(DB_NAME);
        } catch (IOException e) {
            throw new RuntimeException("Error getting the database instance", e);
        } catch (CouchbaseLiteException e) {
            throw new RuntimeException("Error getting the database instance", e);
        }
    }

    @Provides
    public ParticipantDAO getParticipantService() {
        return new ParticipantDAO(this.context);
    }

    @Provides
    public AssessmentDAO getAssessmentDAO(Database database) {
        return new AssessmentDAO(database);
    }

    @Provides
    public AssessmentUploader getAssessmentUploader(Preferences preferences, RestAssessmentService restAssessmentService, AssessmentDAO assessmentDAO) {
        return new AssessmentUploader(preferences, restAssessmentService, assessmentDAO);
    }

    @Singleton
    @Provides
    public ResponseCollector getResponseCollectorService() {
        return new ResponseCollector();
    }

    @Provides
    public SurveyAlarmScheduler getSurveyAlarmScheduler(Gson gson) {
        return new SurveyAlarmScheduler(gson);
    }

    @Singleton
    @Provides
    public Gson getGson() {
        return new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
            .create();
    }

    @Provides
    public SurveyVibrator getSurveyVibrator() {
        return new SurveyVibrator();
    }

    @Singleton
    @Provides
    public WakeLocker getWakeLocker() {
        return new WakeLocker();
    }

    @Singleton
    @Provides
    public AudioPlayerService getAudioPlayerService() {
        return new AudioPlayerService();
    }

    @Provides
    public RegistrationService getRegistrationService(RestUserService restUserService, ParticipantDAO participantDAO) {
        return new RegistrationService(restUserService, participantDAO);
    }

    @Provides
    public Preferences getPreferences() {
        return new Preferences(context);
    }

    @Provides
    public RestAssessmentService getRestAssessmentService(Gson gson) {
        return getRestAdapter(gson).create(RestAssessmentService.class);
    }

    private RestAdapter getRestAdapter(Gson gson) {
        return new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(BuildConfig.API_BASE_URL)
            .setConverter(new GsonConverter(gson))
            .build();
    }

    @Provides
    public RestUserService getRestUserService(Gson gson) {
        return getRestAdapter(gson).create(RestUserService.class);
    }

    @Provides
    @Singleton
    public AssessmentHolder getAssessmentHolder() {
        return new AssessmentHolder();
    }

    @Provides
    public StudyParser getStudyParser() {
        return new StudyParser();
    }

    @Provides
    public ResourceService getResourceService() {
        return new ResourceService();
    }
}
