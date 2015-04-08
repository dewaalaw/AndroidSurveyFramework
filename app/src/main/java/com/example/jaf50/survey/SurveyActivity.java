package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentUiBuilderService;
import com.parse.ParseUser;

import java.util.List;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SurveyActivity extends FragmentActivity {

  @InjectView(R.id.contentPanel)
  LinearLayout contentPanel;

  @InjectView(R.id.mainTextView)
  TextView mainTextView;

  @InjectView(R.id.nextButton)
  BootstrapButton nextButton;

  @InjectView(R.id.previousButton)
  BootstrapButton previousButton;

  private AssessmentState assessmentState;
  private SurveyActivityService surveyActivityService = new SurveyActivityService();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

//    SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
//    SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    ButterKnife.inject(this);

    if (getIntent() != null) {
      String surveyName = getIntent().getStringExtra("surveyName");
      ASYNC_UI_startAssessment(AssessmentHolder.getInstance().getStudyModel(), surveyName);
    }
  }

  @OnClick(R.id.previousButton)
  public void onPrevious() {
    if (surveyActivityService.hasPrevious()) {
      SurveyScreen previousSurveyScreen = surveyActivityService.previous();
      UI_setCurrentScreen(previousSurveyScreen.getScreenId());
    }
  }

  @OnClick(R.id.nextButton)
  public void onNext() {
    Action action = surveyActivityService.getCurrentScreenAction();
    if (action != null) {
      if (action instanceof DirectContentTransition) {
        UI_transition((DirectContentTransition) action);
      } else if (action instanceof EndAssessmentAction) {
        ASYNC_UI_endAssessment();
      }
    }
  }

  private void ASYNC_UI_endAssessment() {
    UI_setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.ASYNC_saveAssessmentNow();
        return null;
      }
    }).continueWith(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        if (task.isFaulted()) {
          Toast.makeText(SurveyActivity.this, "Data upload failed: " + task.getError(), Toast.LENGTH_LONG).show();
          Intent i = new Intent(Intent.ACTION_SEND);
          i.setType("message/rfc822");
          i.putExtra(Intent.EXTRA_EMAIL, new String[]{"josh7up@gmail.com"});
          i.putExtra(Intent.EXTRA_SUBJECT, "Survey submission error");
          i.putExtra(Intent.EXTRA_TEXT, "Failed sending data. Stack trace = " + task.getError());
          try {
            startActivity(Intent.createChooser(i, "Send mail..."));
          } catch (android.content.ActivityNotFoundException ex) {
          }
        } else {
          Assessment currentAssessment = surveyActivityService.getCurrentAssessment();
          Toast.makeText(SurveyActivity.this, "Uploaded data to the server for survey " + currentAssessment.getSurveyName() + " and participant " + currentAssessment.getParticipant().getUsername(), Toast.LENGTH_LONG).show();
          SurveyActivity.this.finish();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void UI_transition(final DirectContentTransition action) {
    if (action.requiresResponse() && !surveyActivityService.getCurrentScreen().responsesEntered()) {
      new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
          .setTitleText("Skip Question?")
          .setContentText("Would you like to skip this question?")
          .setCancelText("No")
          .setConfirmText("Yes")
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              // TODO - Need to mark this screen as being "skipped".
              sweetAlertDialog.dismissWithAnimation();
              surveyActivityService.ASYNC_transitionToNextScreen(action.getToId());
              UI_setCurrentScreen(action.getToId());
            }
          })
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              sweetAlertDialog.dismissWithAnimation();
            }
          }).show();
    } else {
      surveyActivityService.ASYNC_transitionToNextScreen(action.getToId());
      UI_setCurrentScreen(action.getToId());
    }
  }

  public void UI_setAssessmentState(final AssessmentState assessmentState) {
    this.assessmentState = assessmentState;
    switch (assessmentState) {
      case Starting:
        previousButton.setEnabled(true);
        nextButton.setEnabled(true);
        break;
      case Ending:
        previousButton.setEnabled(false);
        nextButton.setEnabled(false);
        break;
    }
  }

  private void UI_setCurrentScreen(String screenId) {
    UI_updateNavigationButtons(surveyActivityService.getCurrentScreen());
    UI_updateMainTextView(surveyActivityService.getCurrentScreen());
    contentPanel.removeAllViews();
    contentPanel.addView(surveyActivityService.getScreen(screenId));
  }

  private void UI_updateMainTextView(SurveyScreen currentScreen) {
    mainTextView.setText(Html.fromHtml(currentScreen.getMainText()));
    mainTextView.setVisibility(TextUtils.isEmpty(currentScreen.getMainText()) ? View.INVISIBLE : View.VISIBLE);
  }

  private void UI_updateNavigationButtons(SurveyScreen currentScreen) {
    if (currentScreen.getPreviousButtonModel().getLabel() != null) {
      UI_setPreviousButtonLabel(currentScreen.getPreviousButtonModel().getLabel());
    }
    if (currentScreen.getNextButtonModel().getLabel() != null) {
      UI_setNextButtonLabel(currentScreen.getNextButtonModel().getLabel());
    }

    if (currentScreen.getPreviousButtonModel().isAllowed()) {
      UI_showPreviousButton();
    } else {
      UI_hidePreviousButton();
    }

    if (currentScreen.getNextButtonModel().isAllowed()) {
      UI_showNextButton();
    } else {
      UI_hideNextButton();
    }
  }

  public void UI_showPreviousButton() {
    previousButton.setVisibility(View.VISIBLE);
  }

  public void UI_showNextButton() {
    nextButton.setVisibility(View.VISIBLE);
  }

  public void UI_hidePreviousButton() {
    previousButton.setVisibility(View.INVISIBLE);
  }

  public void UI_hideNextButton() {
    nextButton.setVisibility(View.INVISIBLE);
  }

  public void UI_setPreviousButtonLabel(String label) {
    previousButton.setText(label);
  }

  public void UI_setNextButtonLabel(String label) {
    nextButton.setText(label);
  }

  private void ASYNC_UI_startAssessment(StudyModel studyModel, String surveyName) {
    SurveyModel surveyModel = surveyActivityService.ASYNC_getSurveyModel(surveyName, studyModel);
    Assessment assessment = new Assessment();
    assessment.setSurveyName(surveyName);
    assessment.setParticipant(ParseUser.getCurrentUser());
    AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(this, assessment);
    final List<SurveyScreen> surveyScreens = assessmentUiBuilderService.build(surveyModel);

    surveyActivityService.ASYNC_setCurrentAssessment(assessment);
    surveyActivityService.ASYNC_setSurveyScreens(surveyScreens);

    Log.d(getClass().getName(), "About to start assessment " + surveyName + ", start screen id = " + surveyScreens.get(0).getScreenId());

    Task.call(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        surveyActivityService.ASYNC_startSurvey(surveyScreens.get(0).getScreenId());
        UI_setCurrentScreen(surveyScreens.get(0).getScreenId());
        UI_setAssessmentState(AssessmentState.Starting);
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
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
      UI_setAssessmentState(AssessmentState.Ending);

      Task.callInBackground(new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          surveyActivityService.ASYNC_saveAssessmentEventually();
          return null;
        }
      }).continueWith(new Continuation<Void, Object>() {
        @Override
        public Object then(Task<Void> task) throws Exception {
          if (task.isFaulted()) {
            // TODO - exception handling.
          } else {
            StudyModel studyModel = AssessmentHolder.getInstance().getStudyModel();
            ASYNC_UI_startAssessment(studyModel, surveyName);
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
