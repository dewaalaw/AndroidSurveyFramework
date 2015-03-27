package com.example.jaf50.survey;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.parse.sdk.BetterFindCallback;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AssessmentUiBuilder;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    final SurveyModel surveyModel = assessmentParserService.parse(getResources().openRawResource(R.raw.survey));

    ParseQuery<Survey> query = ParseQuery.getQuery("Survey");
    query.whereEqualTo("name", "Survey Demo");
    query.fromLocalDatastore();

    query.findInBackground(new BetterFindCallback<Survey>() {
      @Override
      public void onSuccess(List<Survey> results) {
        Survey survey;
        if (results.isEmpty()) {
          survey = new Survey();
          survey.setName("Survey Demo");
          survey.pinInBackground();
          survey.saveInBackground();
        } else {
          survey = results.get(0);
        }

        Assessment assessment = new Assessment();
        assessment.setSurvey(survey);
        assessment.setParticipant(ParseUser.getCurrentUser());

        AssessmentUiBuilder assessmentUiBuilder = new AssessmentUiBuilder(SurveyActivity.this, assessment);
        final List<SurveyScreen> surveyScreens = assessmentUiBuilder.build(surveyModel);

        final SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
        fragment.setCurrentAssessment(assessment);
        for (SurveyScreen screen : surveyScreens) {
          fragment.addSurveyScreen(screen);
        }

        fragment.startSurvey(surveyScreens.get(0).getScreenId());
      }

      @Override
      protected void onFailure(ParseException e) {
        Toast.makeText(SurveyActivity.this, "Error retrieving the survey: " + e, Toast.LENGTH_LONG).show();
      }
    });
  }
}
