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
import com.example.jaf50.survey.domain.AssessmentSaveOptions;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AudioPlayerService;

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

  private static final long timeoutMillis = 60000 * 5;

  private SurveyActivityService surveyActivityService = new SurveyActivityService();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);
    ButterKnife.inject(this);

    String surveyName = getIntent().getStringExtra("surveyName");
    boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
    boolean isTimeout = getIntent().getBooleanExtra("isTimeout", false);
    Log.d(getClass().getName(), "In onCreate(), surveyName = " + surveyName + ", isAlarm = " + isAlarm);

    surveyActivityService.initStudyModel(getResources().openRawResource(R.raw.coop_city));

    if (isAlarm) {
      onAlarmForNewActivity(surveyName);
    } else if (isTimeout) {
      finish();
    } else {
      startSurvey(surveyName);
    }
  }

  private void startSurvey(String surveyName) {
    surveyActivityService.startSurvey(surveyName, AssessmentHolder.getInstance().getStudyModel(), this);
    setCurrentScreen(surveyActivityService.getStartScreenId());
    setAssessmentState(AssessmentState.Starting);
  }

  private void initStudyModel() {
    AssessmentParserService assessmentParserService = new AssessmentParserService();
    StudyModel studyModel = assessmentParserService.parseStudy(getResources().openRawResource(R.raw.coop_city));
    AssessmentHolder.getInstance().setStudyModel(studyModel);
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

  // NOTE: this will also be called if the user taps HOME and then taps the application icon again.
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);
    String surveyName = getIntent().getStringExtra("surveyName");
    boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
    boolean isTimeout = getIntent().getBooleanExtra("isTimeout", false);

    Toast.makeText(this, "In SurveyActivity.onNewIntent(), intent = " + intent, Toast.LENGTH_LONG).show();
    Log.d(getClass().getName(), "In onNewIntent(), surveyName = " + surveyName + ", isAlarm = " + isAlarm + ", isTimeout = " + isTimeout);

    if (isTimeout) {
      Toast.makeText(this, "Timeout occurred!", Toast.LENGTH_LONG).show();
      onTimeout();
    } else if (surveyName != null && isAlarm) {
      onAlarmForExistingActivity(surveyName);
    } else {
      Log.d(getClass().getName(), "In onNewIntent(), can't start the assessment because the surveyName is null.");
    }
  }

  private void onTimeout() {
    Log.d(getClass().getName(), "In onTimeout()");

    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.saveAssessmentEventually(new AssessmentSaveOptions().setTimeout(true));
        return null;
      }
    }).continueWith(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        Log.d(getClass().getName(), "In onTimeout(), continueWith block, task error? " + task.isFaulted());
        if (task.isFaulted()) {
          Log.d(getClass().getName(), "Data upload failed while uploading data for timeout: " + task.toString());
        }
        AssessmentHolder.getInstance().setAssessmentInProgress(false);
        finish();
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void endAssessment() {
    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.saveAssessmentNow(new AssessmentSaveOptions());
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
          AssessmentHolder.getInstance().setAssessmentInProgress(false);
          finish();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void onAlarmForExistingActivity(final String surveyName) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        surveyActivityService.saveAssessmentEventually(new AssessmentSaveOptions());
        return null;
      }
    }).continueWith(new Continuation<Void, Object>() {
      @Override
      public Object then(Task<Void> task) throws Exception {
        if (task.isFaulted()) {
          Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + task.getError(), Toast.LENGTH_LONG).show();
        } else {
          startSurvey(surveyName);
          playAlarmAudio();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  private void onAlarmForNewActivity(String surveyName) {
    startSurvey(surveyName);
    playAlarmAudio();
  }

  private void playAlarmAudio() {
    Log.d(getClass().getName(), "In playAlarmAudio(), surveyName = " + getIntent().getStringExtra("surveyName"));
    AudioPlayerService.getInstance().play(this, R.raw.laid_back_sunday);
  }

  @Override
  protected void onStart() {
    super.onStart();
    AssessmentHolder.getInstance().setAssessmentInProgress(true);
  }

  @Override
  public void onBackPressed() {
  }

  @Override
  protected void onPause() {
    super.onPause();
    Log.d(getClass().getName(), "In onPause()");
  }

  @Override
  protected void onStop() {
    AudioPlayerService.getInstance().stop();
    super.onStop();
  }
}
