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
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentUiBuilderService;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;
import butterknife.ButterKnife;
import butterknife.InjectView;
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

  private HashMap<String, SurveyScreen> surveyScreens = new HashMap<>();
  private SurveyScreen currentScreen;
  private Stack<SurveyScreen> screenStack = new Stack<>();
  private Stack<List<AssessmentResponse>> responseStack = new Stack<>();

  private Assessment currentAssessment;
  private AssessmentState assessmentState;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

//    SchedulerManager.getInstance().saveTask(this, "* * * * *", LaunchSurveyTask.class);
//    SchedulerManager.getInstance().restart(this, LaunchSurveyTask.class);

    ButterKnife.inject(this);

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Action action = currentScreen.getAction();
        if (action != null) {
          if (action instanceof DirectContentTransition) {
            transition((DirectContentTransition) action);
          } else if (action instanceof EndAssessmentAction) {
            endAssessment();
          }
        }
      }
    });

    previousButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (screenStack.size() >= 2) {
          // Remove the current screen from the top of the stack.
          screenStack.pop();
          responseStack.pop();
          // Then peek to get the previous screen.
          SurveyScreen previousSurveyScreen = screenStack.peek();
          setCurrentScreen(previousSurveyScreen.getScreenId());
        }
      }
    });

    if (getIntent() != null) {
      String surveyName = getIntent().getStringExtra("surveyName");
      StudyModel studyModel = AssessmentHolder.getInstance().getStudyModel();
      startAssessment(studyModel, surveyName);
    }
  }

  private void endAssessment() {
    setAssessmentState(AssessmentState.Ending);

    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        saveAssessmentNow();
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
          Toast.makeText(SurveyActivity.this, "Uploaded data to the server for survey " + currentAssessment.getSurveyName() + " and participant " + currentAssessment.getParticipant().getUsername(), Toast.LENGTH_LONG).show();
          SurveyActivity.this.finish();
        }
        return null;
      }
    }, Task.UI_THREAD_EXECUTOR);
  }

  public void saveAssessmentNow() throws ParseException {
    saveAssessment();
    currentAssessment.save();
  }

  public void saveAssessmentEventually() throws ParseException {
    saveAssessment();
    currentAssessment.saveEventually();
  }

  private void saveAssessment() {
    currentAssessment.setResponses(collectResponses());
    currentAssessment.pinInBackground();
  }

  private void transition(final DirectContentTransition action) {
    if (action.requiresResponse() && !currentScreen.responsesEntered()) {
      new SweetAlertDialog(SurveyActivity.this, SweetAlertDialog.NORMAL_TYPE)
          .setTitleText("Skip Question?")
          .setContentText("Would you like to skip this question?")
          .setCancelText("No")
          .setConfirmText("Yes")
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              // TODO - Need to mark this screen as being "skipped".
              sweetAlertDialog.dismissWithAnimation();
              transitionToNextScreen(action.getToId());
            }
          })
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              sweetAlertDialog.dismissWithAnimation();
            }
          }).show();
    } else {
      transitionToNextScreen(action.getToId());
    }
  }

  private void transitionToNextScreen(String toScreenId) {
    responseStack.push(currentScreen.collectResponses());
    setCurrentScreen(toScreenId);
    SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
    screenStack.push(surveyScreen);
  }

  public void setAssessmentState(final AssessmentState assessmentState) {
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

  private List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();
    for (List<AssessmentResponse> responses : responseStack) {
      assessmentResponses.addAll(responses);
    }
    return assessmentResponses;
  }

  public void setSurveyScreens(List<SurveyScreen> surveyScreens) {
    this.surveyScreens.clear();
    for (SurveyScreen surveyScreen : surveyScreens) {
      this.surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
    }
  }

  public void startSurvey(String startScreenId) {
    screenStack.clear();
    // TODO - any other setup upon survey start (e.g. capture start timestamp).
    setCurrentScreen(startScreenId);
    SurveyScreen startSurveyScreen = surveyScreens.get(startScreenId);
    screenStack.push(startSurveyScreen);

    setAssessmentState(AssessmentState.Starting);
  }

  public void setCurrentScreen(String screenId) {
    SurveyScreen surveyScreen = surveyScreens.get(screenId);
    if (surveyScreen == null) {
      throw new IllegalArgumentException("Invalid survey screen id specified: '" + screenId + "'.");
    }
    currentScreen = surveyScreen;
    updateNavigationButtons();
    updateMainTextView();
    contentPanel.removeAllViews();
    contentPanel.addView(surveyScreen);
  }

  public void setCurrentAssessment(Assessment currentAssessment) {
    this.currentAssessment = currentAssessment;
  }

  private void updateMainTextView() {
    mainTextView.setText(Html.fromHtml(currentScreen.getMainText()));
    mainTextView.setVisibility(TextUtils.isEmpty(currentScreen.getMainText()) ? View.INVISIBLE : View.VISIBLE);
  }

  private void updateNavigationButtons() {
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

  private void startAssessment(StudyModel studyModel, String surveyName) {
    SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
    Assessment assessment = createAssessment(surveyName);
    AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(this, assessment);
    final List<SurveyScreen> surveyScreens = assessmentUiBuilderService.build(surveyModel);

    setCurrentAssessment(assessment);
    setSurveyScreens(surveyScreens);

    Log.d(getClass().getName(), "About to start assessment " + surveyName + ", start screen id = " + surveyScreens.get(0).getScreenId());

    Task.call(new Callable<Object>() {
      @Override
      public Object call() throws Exception {
        startSurvey(surveyScreens.get(0).getScreenId());
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
      setAssessmentState(AssessmentState.Ending);

      Task.callInBackground(new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          saveAssessmentEventually();
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
