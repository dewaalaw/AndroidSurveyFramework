package com.askonthego;

import android.content.Context;

import com.askonthego.alarm.SurveyAlarmScheduler;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.service.AssessmentConverter;
import com.askonthego.service.AssessmentParserService;
import com.askonthego.service.AssessmentService;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.DomainSerializationService;
import com.askonthego.service.LocalRegistrationService;
import com.askonthego.service.OnlineRegistrationService;
import com.askonthego.service.Preferences;
import com.askonthego.service.ResponseCollectorService;
import com.askonthego.service.ServiceConstants;
import com.askonthego.service.SurveyActivityService;
import com.google.gson.Gson;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import retrofit.RestAdapter;

@Module(injects = {
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
  public SurveyActivityService getSurveyActivityService() {
    return new SurveyActivityService();
  }

  @Singleton
  @Provides
  public DomainSerializationService getDomainSerializationService() {
    return new DomainSerializationService();
  }

  @Provides
  public AssessmentService getAssessmentService(Preferences preferences, @Named("AssessmentServiceRestAdapter") RestAdapter restAdapter) {
    return new AssessmentService(preferences, restAdapter);
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

  @Provides
  public AssessmentParserService getAssessmentParserService() {
    return new AssessmentParserService();
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
  public OnlineRegistrationService getOnlineRegistrationService(@Named("GenericRestAdapter") RestAdapter restAdapter) {
    return new OnlineRegistrationService(restAdapter);
  }

  @Provides
  public Preferences getPreferences() {
    return new Preferences(context);
  }

  @Provides
  public AssessmentConverter getAssessmentConverter(DomainSerializationService domainSerializationService) {
    return new AssessmentConverter(domainSerializationService);
  }

  @Provides
  @Named("AssessmentServiceRestAdapter")
  public RestAdapter getAssessmentServiceRestAdapter(AssessmentConverter assessmentConverter) {
    return new RestAdapter.Builder()
        .setConverter(assessmentConverter)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setEndpoint(ServiceConstants.API_BASE_URL)
        .build();
  }

  @Provides
  @Named("GenericRestAdapter")
  public RestAdapter getGenericRestAdapter() {
    return new RestAdapter.Builder()
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setEndpoint(ServiceConstants.API_BASE_URL)
        .build();
  }
}
