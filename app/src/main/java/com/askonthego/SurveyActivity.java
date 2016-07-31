package com.askonthego;

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
import com.askonthego.alarm.AlarmEvent;
import com.askonthego.alarm.SurveyVibrator;
import com.askonthego.alarm.TimeoutEvent;
import com.askonthego.alarm.WakeLocker;
import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentSaveOptions;
import com.askonthego.service.AssessmentParser;
import com.askonthego.service.AssessmentService;
import com.askonthego.service.AudioPlayerService;
import com.askonthego.service.StudyParser;
import com.askonthego.service.SurveyActivityService;
import com.askonthego.util.LogUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;
import com.jpardogo.android.googleprogressbar.library.ChromeFloatingCirclesDrawable;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import de.greenrobot.event.EventBus;
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

  @Inject AssessmentService assessmentService;
  @Inject SurveyVibrator surveyVibrator;
  @Inject WakeLocker wakeLocker;
  @Inject AudioPlayerService audioPlayerService;
  @Inject AssessmentHolder assessmentHolder;
  @Inject StudyParser studyParser;

  private boolean onCreateCalled;
  private SurveyActivityService surveyActivityService;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Sheath.inject(this);
    this.surveyActivityService = new SurveyActivityService(new AssessmentParser(this), studyParser, assessmentHolder);

    String surveyName = getIntent().getStringExtra("surveyName");
    LogUtils.d(getClass(), "In onCreate(), surveyName = " + surveyName);

    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    setContentView(R.layout.activity_survey);
    ButterKnife.bind(this);

    this.progressBar.setIndeterminateDrawable(new ChromeFloatingCirclesDrawable.Builder(this).build());
    this.surveyActivityService.initStudyModel(getResources().openRawResource(R.raw.coop_city));
    this.onCreateCalled = true;

    TimeoutEvent timeoutEvent = EventBus.getDefault().getStickyEvent(TimeoutEvent.class);
    if (timeoutEvent != null && !assessmentHolder.isAssessmentInProgress()) {
      EventBus.getDefault().removeStickyEvent(timeoutEvent);
      // If this activity was started as a result of a timeout event, then the user already finished a survey and this was never unscheduled,
      // or the user explicitly killed the app during a survey. Otherwise, this activity would still be on the back stack.
      LogUtils.d(getClass(), "In onCreate() about to finish early in isTimeout block: surveyName == " + surveyName);
      finish();
    } else if (surveyName == null) {
      LogUtils.d(getClass(), "In onCreate() about to finish early in surveyName == null block");
      finish();
    } else {
      if (!assessmentHolder.isAssessmentInProgress()) {
        startSurvey(surveyName);
      }
      assessmentHolder.setAssessmentInProgress(true);
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    TimeoutEvent timeoutEvent = EventBus.getDefault().getStickyEvent(TimeoutEvent.class);
    AlarmEvent alarmEvent = EventBus.getDefault().getStickyEvent(AlarmEvent.class);

    if (timeoutEvent != null) {
      EventBus.getDefault().removeStickyEvent(timeoutEvent);

      wakeLocker.acquirePartial(this);
      LogUtils.d(getClass(), "In TimeoutEvent handler.");
      Toast.makeText(this, "Timeout occurred!", Toast.LENGTH_LONG).show();
      onTimeout();
    } else if (alarmEvent != null) {
      EventBus.getDefault().removeStickyEvent(alarmEvent);

      wakeLocker.acquireFull(this);
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
      LogUtils.d(getClass(), "In AlarmEvent handler, onCreateCalled = " + onCreateCalled + ", surveyName = " + alarmEvent.surveyName);
      if (onCreateCalled) {
        onAlarmForNewActivity(alarmEvent);
      } else {
        onAlarmForExistingActivity(alarmEvent);
      }
    }
  }

  private void onAlarmForNewActivity(AlarmEvent alarmEvent) {
    // No existing survey prior to this call.
    startSurvey(alarmEvent.surveyName);
    soundAlarm();
  }

  private void onAlarmForExistingActivity(final AlarmEvent alarmEvent) {
    // This method was called in response to an alarm. Save the existing assessment's data and start the alarmed survey.
    setAssessmentState(AssessmentState.Ending);

    final Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    assessmentService.save(currentAssessment, new Callback<Void>() {
      @Override
      public void success(Void aVoid, Response response) {
        LogUtils.d(AssessmentService.class, "Saved assessment successfully: " + response.getBody());
        Toast.makeText(SurveyActivity.this, "Data upload success!", Toast.LENGTH_LONG).show();
        startSurvey(alarmEvent.surveyName);
        soundAlarm();
      }

      @Override
      public void failure(RetrofitError error) {
        LogUtils.e(AssessmentService.class, "Error posting assessment", error);
        Toast.makeText(SurveyActivity.this, "Data upload failed when ending assessment for alarm: " + error, Toast.LENGTH_LONG).show();
        startSurvey(alarmEvent.surveyName);
        soundAlarm();
      }
    });
  }

  private void startSurvey(String surveyName) {
    LogUtils.d(getClass(), "In startSurvey(), surveyName = " + surveyName);

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

    surveyActivityService.startSurvey(surveyName, assessmentHolder.getStudyModel(), this);
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
        // TODO - EndAssessmentAction is currently only used as a tagging interface to indicate that a survey
        // should be completed. There has to be a better way to do this.
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

  private void onTimeout() {
    LogUtils.d(getClass(), "In onTimeout()");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions().setTimeout(true));
    saveAssessment(currentAssessment);
  }

  private void saveAssessment(Assessment currentAssessment) {
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
    assessmentHolder.setAssessmentInProgress(false);
  }

  private void onAssessmentSaveFailure(RetrofitError error) {
    LogUtils.e(getClass(), "Error posting assessment: ", error);
    Toast.makeText(SurveyActivity.this, "Data sync failed: " + error, Toast.LENGTH_LONG).show();
    assessmentHolder.setAssessmentInProgress(false);
  }

  private void endAssessment() {
    LogUtils.d(getClass(), "In endAssessment(), about to save data.");

    setAssessmentState(AssessmentState.Ending);
    Assessment currentAssessment = surveyActivityService.collectAssessment(new AssessmentSaveOptions());
    saveAssessment(currentAssessment);
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
    stopAlarm();
    if (isFinishing()) {
      this.onCreateCalled = false;
    }
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
