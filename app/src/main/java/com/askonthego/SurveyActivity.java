package com.askonthego;

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

import com.askonthego.actions.Action;
import com.askonthego.actions.DirectContentTransition;
import com.askonthego.actions.EndAssessmentAction;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentSaveOptions;
import com.askonthego.service.AssessmentService;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.SurveyActivityService;
import com.askonthego.util.LogUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.R;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import io.pristine.sheath.Sheath;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SurveyActivity extends FragmentActivity {

  @Bind(R.id.contentPanel) LinearLayout contentPanel;
  @Bind(R.id.mainTextView) TextView mainTextView;
  @Bind(R.id.nextButton) BootstrapButton nextButton;
  @Bind(R.id.previousButton) BootstrapButton previousButton;
  @Bind(R.id.progressBar) ProgressBar progressBar;
  @Bind(R.id.progressView) View progressView;
  @Bind(R.id.surveyContentLayout) ViewGroup surveyContentLayout;

  @Inject SurveyActivityService surveyActivityService;
  @Inject AssessmentService assessmentService;
  @Inject SurveyVibrator surveyVibrator;
  @Inject WakeLocker wakeLocker;
  @Inject AudioPlayerService audioPlayerService;

  private boolean onCreateCalled;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Sheath.inject(this);

    LogUtils.d(getClass(), "In onCreate()");

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    setContentView(R.layout.activity_survey);
    ButterKnife.bind(this);

    progressBar.setIndeterminateDrawable(new ChromeFloatingCirclesDrawable.Builder(this).build());

    surveyActivityService.initStudyModel(getResources().openRawResource(R.raw.coop_city));
    this.onCreateCalled = true;

    if (getIntent().getBooleanExtra("isTimeout", false) || getIntent().getStringExtra("surveyName") == null) {
      LogUtils.d(getClass(), "In onCreate() about to finish early: isTimeout == " + getIntent().getBooleanExtra("isTimeout", false) + ", surveyName == " + getIntent().getStringExtra("surveyName"));
      finish();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    String surveyName = getIntent().getStringExtra("surveyName");
    boolean isAlarm = getIntent().getBooleanExtra("isAlarm", false);
    boolean isTimeout = getIntent().getBooleanExtra("isTimeout", false);

    if (isAlarm) { wakeLocker.acquireFull(this); }
    if (isTimeout) { wakeLocker.acquirePartial(this); }

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
    assessmentService.save(currentAssessment, new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        LogUtils.d(AssessmentService.class, "Saved assessment successfully: " + response.getBody());
        Toast.makeText(SurveyActivity.this, "Data upload success!", Toast.LENGTH_LONG).show();
        startSurvey(surveyName);
        soundAlarm();
      }

      @Override
      public void failure(RetrofitError error) {
        LogUtils.e(AssessmentService.class, "Error posting assessment", error);
        Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + error, Toast.LENGTH_LONG).show();
        startSurvey(surveyName);
        soundAlarm();
      }
    });
  }

  private void startSurvey(String surveyName) {
    assessmentService.uploadUnsyncedAssessments(new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        onAssessmentSaveSuccess(response);
      }

      @Override
      public void failure(RetrofitError error) {
        onAssessmentSaveFailure(error);
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
        if (directContentTransition.isResponseRequired() && !surveyActivityService.getCurrentScreen().responsesEntered()) {
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

    if (currentScreen.getPreviousButtonModel().getAllowed()) {
      showPreviousButton();
    } else {
      hidePreviousButton();
    }

    if (currentScreen.getNextButtonModel().getAllowed()) {
      showNextButton();
    } else {
      hideNextButton();
    }
  }

  public void showPreviousButton() { previousButton.setVisibility(View.VISIBLE); }
  public void showNextButton() { nextButton.setVisibility(View.VISIBLE); }
  public void hidePreviousButton() { previousButton.setVisibility(View.INVISIBLE); }
  public void hideNextButton() { nextButton.setVisibility(View.INVISIBLE); }
  public void setPreviousButtonLabel(String label) { previousButton.setText(label); }
  public void setNextButtonLabel(String label) { nextButton.setText(label); }

  // NOTE: this will also be called if the user taps HOME and then taps the application icon again.
  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    LogUtils.d(getClass(), "In onNewIntent()");
    setIntent(intent);
  }

  private void onTimeout() {
    LogUtils.d(getClass(), "In onTimeout()");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions().setTimeout(true));
    assessmentService.save(currentAssessment, new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        onAssessmentSaveSuccess(response);
        finish();
      }

      @Override
      public void failure(RetrofitError error) {
        onAssessmentSaveFailure(error);
        finish();
      }
    });
  }

  private void onAssessmentSaveSuccess(Response response) {
    LogUtils.d(getClass(), "Saved assessment successfully: " + response.getBody());
    Toast.makeText(this, "Synced data successfully: " + response.getBody(), Toast.LENGTH_LONG).show();
    AssessmentHolder.getInstance().setAssessmentInProgress(false);
  }

  private void onAssessmentSaveFailure(RetrofitError error) {
    LogUtils.e(getClass(), "Error posting assessment: ", error);
    Toast.makeText(SurveyActivity.this, "Data sync failed: " + error, Toast.LENGTH_LONG).show();
    AssessmentHolder.getInstance().setAssessmentInProgress(false);
  }

  private void endAssessment() {
    LogUtils.d(getClass(), "In endAssessment(), about to save data.");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    assessmentService.save(currentAssessment, new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        onAssessmentSaveSuccess(response);
        finish();
      }

      @Override
      public void failure(RetrofitError error) {
        onAssessmentSaveFailure(error);
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
        audioPlayerService.play(SurveyActivity.this, R.raw.laid_back_sunday);
        surveyVibrator.vibrate(SurveyActivity.this);
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    LogUtils.d(getClass(), "In onPause()");
    wakeLocker.release();
  }

  @Override
  protected void onStop() {
    stopAlarm();
    this.onCreateCalled = false;
    super.onStop();
  }

  private void stopAlarm() {
    audioPlayerService.stop();
    surveyVibrator.cancelVibrate(this);
  }

  /**
   * Prevent the user from "backing out" of the survey.
   */
  @Override
  public void onBackPressed() {
  }
}
