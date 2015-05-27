package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
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
import com.example.jaf50.survey.util.LogUtils;
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
  private AssessmentService assessmentService = new AssessmentService();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);
    ButterKnife.inject(this);

    String surveyName = getIntent().getStringExtra("surveyName");
    boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
    boolean isTimeout = getIntent().getBooleanExtra("isTimeout", false);
    LogUtils.d(getClass(), "In onCreate(), surveyName = " + surveyName + ", isAlarm = " + isAlarm);

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
    assessmentService.uploadUnsyncedAssessments(this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onAssessmentSaveSuccess(statusCode, headers, responseBody);
      }
      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onAssessmentSaveFailure(statusCode, headers, responseBody, error);
      }
    });

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
        DirectContentTransition directContentTransition = (DirectContentTransition) action;
        if (directContentTransition.requiresResponse() && !surveyActivityService.getCurrentScreen().responsesEntered()) {
          showSkipDialog(directContentTransition);
        } else {
          surveyActivityService.transitionToScreen(directContentTransition.getToId());
          setCurrentScreen(directContentTransition.getToId());
        }
      } else if (action instanceof EndAssessmentAction) {
        endAssessment();
      }
    }
  }

  private void showSkipDialog(final DirectContentTransition directContentTransition) {
    new SweetAlertDialog(this, SweetAlertDialog.NORMAL_TYPE)
        .setTitleText("Skip Question?")
        .setContentText("Would you like to skip this question?")
        .setCancelText("No")
        .setConfirmText("Yes")
        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
          @Override
          public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismissWithAnimation();
            surveyActivityService.transitionToScreen(directContentTransition.getToId());
            setCurrentScreen(directContentTransition.getToId());
          }
        })
        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
          @Override
          public void onClick(SweetAlertDialog sweetAlertDialog) {
            sweetAlertDialog.dismissWithAnimation();
          }
        })
        .show();
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
    LogUtils.d(getClass(), "In onNewIntent(), surveyName = " + surveyName + ", isAlarm = " + isAlarm + ", isTimeout = " + isTimeout);

    if (isTimeout) {
      Toast.makeText(this, "Timeout occurred!", Toast.LENGTH_LONG).show();
      onTimeout();
    } else if (surveyName != null && isAlarm) {
      onAlarmForExistingActivity(surveyName);
    } else {
      LogUtils.d(getClass(), "In onNewIntent(), can't start the assessment because the surveyName is null.");
    }
  }

  private void onTimeout() {
    LogUtils.d(getClass(), "In onTimeout()");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions().setTimeout(true));
    assessmentService.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onAssessmentSaveSuccess(statusCode, headers, responseBody);
        finish();
      }
      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onAssessmentSaveFailure(statusCode, headers, responseBody, error);
        finish();
      }
    });
  }

  private void onAssessmentSaveSuccess(int statusCode, Header[] headers, byte[] responseBody) {
    LogUtils.d(AssessmentService.class, "Saved assessment successfully: " + new String(responseBody));
    Toast.makeText(SurveyActivity.this, "Synced data successfully: " + new String(responseBody), Toast.LENGTH_LONG).show();
    AssessmentHolder.getInstance().setAssessmentInProgress(false);
  }

  private void onAssessmentSaveFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    String response = responseBody != null ? new String(responseBody) : "";
    LogUtils.e(AssessmentService.class, "Error posting assessment: " + response, error);
    Toast.makeText(SurveyActivity.this, "Data sync failed: " + error, Toast.LENGTH_LONG).show();
    AssessmentHolder.getInstance().setAssessmentInProgress(false);
  }

  private void endAssessment() {
    LogUtils.d(getClass(), "In endAssessment(), about to save data.");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    assessmentService.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        onAssessmentSaveSuccess(statusCode, headers, responseBody);
        finish();
      }
      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onAssessmentSaveFailure(statusCode, headers, responseBody, error);
        finish();
      }
    });
  }

  private void onAlarmForExistingActivity(final String surveyName) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    // TODO - don't wait for this assessment to save before moving on to the alarmed assessment.
    assessmentService.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        LogUtils.d(AssessmentService.class, "Saved assessment successfully: " + new String(responseBody));
        startSurvey(surveyName);
        playAlarmAudio();
      }
      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        LogUtils.e(AssessmentService.class, "Error posting assessment: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + error, Toast.LENGTH_LONG).show();
      }
    });
  }

  private void onAlarmForNewActivity(String surveyName) {
    startSurvey(surveyName);
    playAlarmAudio();
  }

  private void playAlarmAudio() {
    LogUtils.d(getClass(), "In playAlarmAudio(), surveyName = " + getIntent().getStringExtra("surveyName"));
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
    LogUtils.d(getClass(), "In onPause()");
  }

  @Override
  protected void onStop() {
    AudioPlayerService.getInstance().stop();
    super.onStop();
  }
}
