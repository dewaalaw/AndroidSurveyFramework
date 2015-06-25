package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.alarm.SurveyVibrator;
import com.example.jaf50.survey.alarm.WakeLocker;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentSaveOptions;
import com.example.jaf50.survey.service.AssessmentService;
import com.example.jaf50.survey.service.AudioPlayerService;
import com.example.jaf50.survey.util.LogUtils;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;
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

  @InjectView(R.id.progressBar)
  ProgressBar progressBar;

  @InjectView(R.id.progressView)
  View progressView;

  @InjectView(R.id.surveyContentLayout)
  ViewGroup surveyContentLayout;

  private SurveyActivityService surveyActivityService = new SurveyActivityService();
  private AssessmentService assessmentService = new AssessmentService();

  private boolean onCreateCalled;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    setContentView(R.layout.activity_survey);
    ButterKnife.inject(this);
    progressBar.setIndeterminateDrawable(new ChromeFloatingCirclesDrawable.Builder(this).build());

    surveyActivityService.initStudyModel(getResources().openRawResource(R.raw.coop_city));
    this.onCreateCalled = true;

    if (getIntent().getBooleanExtra("isTimeout", false)) {
      finish();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();

    String surveyName = getIntent().getStringExtra("surveyName");
    boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
    boolean isTimeout = getIntent().getBooleanExtra("isTimeout", false);

    Toast.makeText(this, "In SurveyActivity.onResume()", Toast.LENGTH_LONG).show();
    LogUtils.d(getClass(), "In onResume(), surveyName = " + surveyName + ", isAlarm = " + isAlarm + ", isTimeout = " + isTimeout);

    if (isTimeout) {
      Toast.makeText(this, "Timeout occurred!", Toast.LENGTH_LONG).show();
      getIntent().putExtra("isTimeout", false);
      onTimeout();
    } else if (isAlarm) {
      getIntent().putExtra("isAlarm", false);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      if (surveyName != null) {
        if (onCreateCalled) {
          // No existing survey prior to this call.
          startSurvey(surveyName);
          soundAlarm();
        } else {
          onAlarmForExistingActivity(surveyName);
        }
      }
    } else if (surveyName != null && !AssessmentHolder.getInstance().isAssessmentInProgress()) {
      startSurvey(surveyName);
    }

    AssessmentHolder.getInstance().setAssessmentInProgress(true);
  }

  private void onAlarmForExistingActivity(final String surveyName) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    assessmentService.save(currentAssessment, this, new AsyncHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        LogUtils.d(AssessmentService.class, "Saved assessment successfully: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Data upload success!", Toast.LENGTH_LONG).show();
        startSurvey(surveyName);
        soundAlarm();
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        LogUtils.e(AssessmentService.class, "Error posting assessment: " + new String(responseBody));
        Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + error, Toast.LENGTH_LONG).show();
        startSurvey(surveyName);
        soundAlarm();
      }
    });
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
      stopAlarm();
      getWindow().clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
        surveyContentLayout.setVisibility(View.VISIBLE);
        progressView.setVisibility(View.INVISIBLE);
        break;
      case Ending:
        surveyContentLayout.setVisibility(View.INVISIBLE);
        progressView.setVisibility(View.VISIBLE);
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
    LogUtils.d(getClass(), "Saved assessment successfully: " + new String(responseBody));
    Toast.makeText(this, "Synced data successfully: " + new String(responseBody), Toast.LENGTH_LONG).show();
    AssessmentHolder.getInstance().setAssessmentInProgress(false);
  }

  private void onAssessmentSaveFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
    String response = responseBody != null ? new String(responseBody) : "";
    LogUtils.e(getClass(), "Error posting assessment: " + response, error);
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

  private void soundAlarm() {
    LogUtils.d(getClass(), "In soundAlarm(), surveyName = " + getIntent().getStringExtra("surveyName"));
    // The audio/vibration are not guaranteed to playback unless the UI is fully visible, and posting a
    // runnable seems to be a reliable way to ensure this.
    surveyContentLayout.post(new Runnable() {
      @Override
      public void run() {
        AudioPlayerService.getInstance().play(SurveyActivity.this, R.raw.laid_back_sunday);
        SurveyVibrator.vibrate(SurveyActivity.this);
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    LogUtils.d(getClass(), "In onPause()");
    WakeLocker.release();
  }

  @Override
  protected void onStop() {
    stopAlarm();
    this.onCreateCalled = false;
    super.onStop();
  }

  private void stopAlarm() {
    AudioPlayerService.getInstance().stop();
    SurveyVibrator.cancelVibrate(this);
  }

  /**
   * Prevent the user from "backing out" of the survey.
   */
  @Override
  public void onBackPressed() {
  }
}
