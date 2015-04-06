package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.buzzbox.mob.android.scheduler.SchedulerManager;
import com.example.jaf50.survey.alarm.LaunchSurveyTask;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentUiBuilderService;
import com.parse.ParseUser;

import java.util.List;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
    SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    StudyModel studyModel = AssessmentHolder.getInstance().getStudyModel();
    if (getIntent() != null) {
      String surveyName = getIntent().getStringExtra("surveyName");

      SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
      Assessment assessment = getAssessment(surveyName);
      AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(this, assessment);
      final List<SurveyScreen> surveyScreens = assessmentUiBuilderService.build(surveyModel);

      final SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
      fragment.setCurrentAssessment(assessment);
      for (SurveyScreen screen : surveyScreens) {
        fragment.addSurveyScreen(screen);
      }

      fragment.startSurvey(surveyScreens.get(0).getScreenId());
    }
  }

  private SurveyModel getSurveyModel(String surveyName, StudyModel studyModel) {
    for (SurveyModel surveyModel : studyModel.getSurveys()) {
      if (surveyModel.getName().equals(surveyName)) {
        return surveyModel;
      }
    }
    return null;
  }

  private Assessment getAssessment(String surveyName) {
    Assessment assessment = new Assessment();
    assessment.setSurveyName(surveyName);
    assessment.setParticipant(ParseUser.getCurrentUser());
    return assessment;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    Toast.makeText(this, "In onNewIntent(), intent = " + intent, Toast.LENGTH_LONG).show();
    Log.d(getClass().getName(), "In onNewIntent(), surveyName = " + getIntent().getStringExtra("surveyName"));
  }

  @Override
  protected void onResume() {
    super.onResume();
    SurveyApplication.setCurrentActivityClass(getClass());
  }

  @Override
  public void onBackPressed() {
  }
}
