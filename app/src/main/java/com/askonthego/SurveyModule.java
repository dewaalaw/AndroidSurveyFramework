package com.askonthego;

import android.content.Context;

import com.askonthego.alarm.AssessmentTimeoutTask;
import com.askonthego.alarm.SurveyAlarmScheduler;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.service.AssessmentConverter;
import com.askonthego.service.AssessmentService;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.DomainSerializationService;
import com.askonthego.service.LocalRegistrationService;
import com.askonthego.service.OnlineRegistrationService;
import com.askonthego.service.Preferences;
import com.askonthego.service.ResponseCollectorService;
import com.askonthego.service.RestAssessmentService;
import com.askonthego.service.RestUserService;
import com.askonthego.service.ServiceConstants;
import com.askonthego.service.StudyParser;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(injects = {
    AssessmentTimeoutTask.class,
    SurveyActivity.class,
    SurveyScreen.class,
    RegisterActivity.class,
    WelcomeActivity.class
})
class SurveyModule {

    private Context context;

    public SurveyModule(Context context) {
        this.context = context;
    }

    @Provides
    public AssessmentService getAssessmentService(Preferences preferences, RestAssessmentService restAssessmentService) {
        return new AssessmentService(preferences, restAssessmentService);
    }

    @Singleton
    @Provides
    public ResponseCollectorService getResponseCollectorService() {
        return new ResponseCollectorService();
    }

    @Provides
    public SurveyAlarmScheduler getSurveyAlarmScheduler(Gson gson) {
        return new SurveyAlarmScheduler(gson);
    }

    @Singleton
    @Provides
    public Gson getGson() {
        return new Gson();
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
    public LocalRegistrationService getLocalRegistrationService() {
        return new LocalRegistrationService();
    }

    @Provides
    public OnlineRegistrationService getOnlineRegistrationService(RestUserService restUserService) {
        return new OnlineRegistrationService(restUserService);
    }

    @Provides
    public Preferences getPreferences() {
        return new Preferences(context);
    }

    @Provides
    public RestAssessmentService getRetrofitAssessmentService() {
        RestAdapter restAdapter = getAssessmentServiceRestAdapter();
        return restAdapter.create(RestAssessmentService.class);
    }

    private RestAdapter getAssessmentServiceRestAdapter() {
        DomainSerializationService domainSerializationService = new DomainSerializationService();
        AssessmentConverter assessmentConverter = new AssessmentConverter(domainSerializationService);
        return getBaseRestAdapterBuilder()
            .setConverter(assessmentConverter)
            .build();
    }

    private RestAdapter.Builder getBaseRestAdapterBuilder() {
        return new RestAdapter.Builder()
            .setLogLevel(RestAdapter.LogLevel.FULL)
            .setEndpoint(ServiceConstants.API_BASE_URL);
    }

    @Provides
    public RestUserService getUserService() {
        RestAdapter restAdapter = getBaseRestAdapterBuilder().build();
        return restAdapter.create(RestUserService.class);
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
}
