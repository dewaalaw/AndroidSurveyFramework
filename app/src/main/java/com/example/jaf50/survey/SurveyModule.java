package com.example.jaf50.survey;

import com.example.jaf50.survey.service.AssessmentService;
import com.example.jaf50.survey.service.ResponseCollectorService;
import com.example.jaf50.survey.service.SurveyActivityService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(injects = {SurveyActivity.class, SurveyScreen.class}, library = true)
class SurveyModule {

  @Provides
  public SurveyActivityService getSurveyActivityService() {
    return new SurveyActivityService();
  }

  @Provides
  public AssessmentService getAssessmentService() {
    return new AssessmentService();
  }

  @Singleton
  @Provides
  public ResponseCollectorService getResponseCollectorService() {
    return new ResponseCollectorService();
  }
}
