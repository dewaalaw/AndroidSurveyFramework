package com.askonthego;

import com.askonthego.alarm.SurveyAlarmScheduler;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.service.AssessmentParserService;
import com.askonthego.service.AssessmentService;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.DomainSerializationService;
import com.askonthego.service.LocalRegistrationService;
import com.askonthego.service.OnlineRegistrationService;
import com.askonthego.service.ResponseCollectorService;
import com.askonthego.service.SurveyActivityService;
import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {
    SurveyActivity.class,
    SurveyScreen.class,
    RegisterActivity.class,
    WelcomeActivity.class
})
class SurveyModule {

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
  public AssessmentService getAssessmentService(DomainSerializationService domainSerializationService) {
    return new AssessmentService(domainSerializationService);
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
  public OnlineRegistrationService getOnlineRegistrationService() {
    return new OnlineRegistrationService();
  }
}
