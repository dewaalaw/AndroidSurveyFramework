package com.example.jaf50.survey;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AssessmentUiBuilder;
import com.parse.ParseUser;

import java.util.List;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    SurveyModel surveyModel = assessmentParserService.parse(getResources().openRawResource(R.raw.survey));

    Survey survey = new Survey();
    survey.setName("My Survey");

    Assessment assessment = new Assessment();
    assessment.setSurvey(survey);
    assessment.setParticipant(ParseUser.getCurrentUser());

    AssessmentUiBuilder assessmentUiBuilder = new AssessmentUiBuilder(this, assessment);
    final List<SurveyScreen> surveyScreens = assessmentUiBuilder.build(surveyModel);

    final SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    fragment.setCurrentAssessment(assessment);
    for (SurveyScreen screen : surveyScreens) {
      fragment.addSurveyScreen(screen);
    }

    fragment.startSurvey(surveyScreens.get(0).getScreenId());
  }
}
