package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.example.jaf50.survey.domain.Assessment;
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

    //SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
    //SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    final SurveyModel surveyModel = assessmentParserService.parse(getResources().openRawResource(R.raw.real_survey));

    Assessment assessment = new Assessment();
    assessment.setSurveyName("Beeped");
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
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Toast.makeText(this, "In onNewIntent(), intent = " + intent, Toast.LENGTH_LONG).show();
  }
}
