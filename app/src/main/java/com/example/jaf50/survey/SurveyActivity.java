package com.example.jaf50.survey;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AssessmentUiBuilder;

import java.util.List;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    SurveyModel surveyModel = assessmentParserService.parse(getResources().openRawResource(R.raw.survey));

    LayoutInflater inflator = LayoutInflater.from(this);
    AssessmentUiBuilder assessmentUiBuilder = new AssessmentUiBuilder(inflator);
    List<SurveyScreen> surveyScreens = assessmentUiBuilder.build(surveyModel);

    SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    for (SurveyScreen screen : surveyScreens) {
      fragment.addSurveyScreen(screen);
    }

    fragment.startSurvey(surveyScreens.get(0).getScreenId());
  }
}
