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
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
    SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    if (getIntent() != null) {
      String surveyName = getIntent().getStringExtra("surveyName");
      StudyModel studyModel = AssessmentHolder.getInstance().getStudyModel();
      startAssessment(studyModel, surveyName);
    }
  }

  private void startAssessment(StudyModel studyModel, String surveyName) {
    SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
    Assessment assessment = createAssessment(surveyName);
    AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(this, assessment);
    final List<SurveyScreen> surveyScreens = assessmentUiBuilderService.build(surveyModel);

    final SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    fragment.setCurrentAssessment(assessment);
    fragment.setSurveyScreens(surveyScreens);

    Log.d(getClass().getName(), "About to start assessment " + surveyName + ", start screen id = " + surveyScreens.get(0).getScreenId());

    Task.call(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        fragment.startSurvey(surveyScreens.get(0).getScreenId());
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private SurveyModel getSurveyModel(String surveyName, StudyModel studyModel) {
    for (SurveyModel surveyModel : studyModel.getSurveys()) {
      if (surveyModel.getName().equals(surveyName)) {
        return surveyModel;
      }
    }
    return null;
  }

  private Assessment createAssessment(String surveyName) {
    Assessment assessment = new Assessment();
    assessment.setSurveyName(surveyName);
    assessment.setParticipant(ParseUser.getCurrentUser());
    return assessment;
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);

    final String surveyName = getIntent().getStringExtra("surveyName");

    Toast.makeText(this, "In onNewIntent(), intent = " + intent, Toast.LENGTH_LONG).show();
    Log.d(getClass().getName(), "In onNewIntent(), surveyName = " + surveyName);

    if (surveyName != null) {
      // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
      final SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
      fragment.setAssessmentState(AssessmentState.Ending);

      Task.callInBackground(new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          fragment.saveAssessmentEventually();
          return null;
        }
      }).continueWith(new Continuation<Void, Object>() {
        @Override
        public Object then(Task<Void> task) throws Exception {
          if (task.isFaulted()) {
            // TODO - exception handling.
          } else {
            StudyModel studyModel = AssessmentHolder.getInstance().getStudyModel();
            startAssessment(studyModel, surveyName);
          }
          return null;
        }
      });
    } else {
      Log.d(getClass().getName(), "In onNewIntent(), can't start the assessment because the surveyName is null.");
    }
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
