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

  private SurveyActivityService surveyActivityService = new SurveyActivityService();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);
    ButterKnife.inject(this);

//    SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
//    SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    if (getIntent() != null) {
      String surveyName = getIntent().getStringExtra("surveyName");
      surveyActivityService.initAssessment(surveyName, AssessmentHolder.getInstance().getStudyModel(), this);
      surveyActivityService.startSurvey();
      setCurrentScreen(surveyActivityService.getStartScreenId());
      setAssessmentState(AssessmentState.Starting);
    }
  }

  @OnClick(R.id.previousButton)
  public void onPrevious() {
    if (surveyActivityService.hasPrevious()) {
      SurveyScreen previousSurveyScreen = surveyActivityService.previous();
      setCurrentScreen(previousSurveyScreen.getScreenId());
    }
  }

  @OnClick(R.id.nextButton)
  public void onNext() {
    Action action = surveyActivityService.getCurrentScreenAction();
    if (action != null) {
      if (action instanceof DirectContentTransition) {
        transition((DirectContentTransition) action);
      } else if (action instanceof EndAssessmentAction) {
        endAssessment();
      }
    }
  }

  private void transition(final DirectContentTransition action) {
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
              surveyActivityService.transitionToScreen(action.getToId());
              setCurrentScreen(action.getToId());
            }
          })
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              sweetAlertDialog.dismissWithAnimation();
            }
          }).show();
    } else {
      surveyActivityService.transitionToScreen(action.getToId());
      setCurrentScreen(action.getToId());
    }
  }

  public void setAssessmentState(final AssessmentState assessmentState) {
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

  private void setCurrentScreen(String screenId) {
    updateNavigationButtons(surveyActivityService.getCurrentScreen());
    updateMainTextView(surveyActivityService.getCurrentScreen());
    contentPanel.removeAllViews();
    contentPanel.addView(surveyActivityService.getScreen(screenId));
  }

  private void updateMainTextView(SurveyScreen currentScreen) {
    mainTextView.setText(Html.fromHtml(currentScreen.getMainText()));
    mainTextView.setVisibility(TextUtils.isEmpty(currentScreen.getMainText()) ? View.INVISIBLE : View.VISIBLE);
  }

  private void updateNavigationButtons(SurveyScreen currentScreen) {
    if (currentScreen.getPreviousButtonModel().getLabel() != null) {
      setPreviousButtonLabel(currentScreen.getPreviousButtonModel().getLabel());
    }
    if (currentScreen.getNextButtonModel().getLabel() != null) {
      setNextButtonLabel(currentScreen.getNextButtonModel().getLabel());
    }

    if (currentScreen.getPreviousButtonModel().isAllowed()) {
      showPreviousButton();
    } else {
      hidePreviousButton();
    }

    if (currentScreen.getNextButtonModel().isAllowed()) {
      showNextButton();
    } else {
      hideNextButton();
    }
  }

  public void showPreviousButton() {
    previousButton.setVisibility(View.VISIBLE);
  }

  public void showNextButton() {
    nextButton.setVisibility(View.VISIBLE);
  }

  public void hidePreviousButton() {
    previousButton.setVisibility(View.INVISIBLE);
  }

  public void hideNextButton() {
    nextButton.setVisibility(View.INVISIBLE);
  }

  public void setPreviousButtonLabel(String label) {
    previousButton.setText(label);
  }

  public void setNextButtonLabel(String label) {
    nextButton.setText(label);
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    String surveyName = getIntent().getStringExtra("surveyName");

    Toast.makeText(this, "In onNewIntent(), intent = " + intent, Toast.LENGTH_LONG).show();
    Log.d(getClass().getName(), "In onNewIntent(), surveyName = " + surveyName);

    if (surveyName != null) {
      onAlarm(surveyName);
    } else {
      Log.d(getClass().getName(), "In onNewIntent(), can't start the assessment because the surveyName is null.");
    }
  }

  private void endAssessment() {
    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.saveAssessmentNow();
        return null;
      }
    }).continueWith(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        if (task.isFaulted()) {
          Toast.makeText(SurveyActivity.this, "Data upload failed: " + task.getError(), Toast.LENGTH_LONG).show();
        } else {
          Assessment currentAssessment = surveyActivityService.getCurrentAssessment();
          Toast.makeText(SurveyActivity.this, "Uploaded data to the server for survey " + currentAssessment.getSurveyName() + " and participant " + currentAssessment.getParticipant().getUsername(), Toast.LENGTH_LONG).show();
          SurveyActivity.this.finish();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void onAlarm(final String surveyName) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.saveAssessmentEventually();
        return null;
      }
    }).continueWith(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        if (task.isFaulted()) {
          Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + task.getError(), Toast.LENGTH_LONG).show();
        } else {
          surveyActivityService.initAssessment(surveyName, AssessmentHolder.getInstance().getStudyModel(), SurveyActivity.this);
          surveyActivityService.startSurvey();
          setCurrentScreen(surveyActivityService.getStartScreenId());
          setAssessmentState(AssessmentState.Starting);
        }
        return null;
      }
    });
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
