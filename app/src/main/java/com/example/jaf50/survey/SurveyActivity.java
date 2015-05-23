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
import com.example.jaf50.survey.service.AssessmentService;
import com.example.jaf50.survey.service.AudioPlayerService;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

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

    AssessmentService service = new AssessmentService();
    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions().setTimeout(true));
    service.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.d(getClass().getName(), "In onTimeout(), saved assessment.");
        Toast.makeText(SurveyActivity.this, "Uploaded data to the server for survey " + currentAssessment.getSurveyName() + " and participant " + currentAssessment.getParticipant().getUsername(), Toast.LENGTH_LONG).show();
        AssessmentHolder.getInstance().setAssessmentInProgress(false);
        finish();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.e(AssessmentService.class.getName(), "Error posting assessment in onTimeout(): " + new String(responseBody));
      }
    });
  }

  private void endAssessment() {
    setAssessmentState(AssessmentState.Ending);

    AssessmentService service = new AssessmentService();
    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    service.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.d(AssessmentService.class.getName(), "Saved assessment successfully: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Uploaded data to the server for survey " + currentAssessment.getSurveyName() + " and participant " + currentAssessment.getParticipant().getUsername(), Toast.LENGTH_LONG).show();
        AssessmentHolder.getInstance().setAssessmentInProgress(false);
        finish();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.e(AssessmentService.class.getName(), "Error posting assessment: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Data upload failed: " + error, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void onAlarmForExistingActivity(final String surveyName) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    AssessmentService service = new AssessmentService();
    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    // TODO - don't wait for this assessment to save before moving on to the alarmed assessment.
    service.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        Log.d(AssessmentService.class.getName(), "Saved assessment successfully: " + new String(responseBody));
        startSurvey(surveyName);
        playAlarmAudio();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.e(AssessmentService.class.getName(), "Error posting assessment: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + error, Toast.LENGTH_LONG).show();
      }
    });
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
