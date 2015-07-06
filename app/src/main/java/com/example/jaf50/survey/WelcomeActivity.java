package com.example.jaf50.survey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.alarm.SurveyAlarmScheduler;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.WelcomeLinkModel;
import com.example.jaf50.survey.parser.WelcomeModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.util.LogUtils;

import java.util.concurrent.Callable;

import bolts.Task;
import butterknife.Bind;
import butterknife.ButterKnife;

public class WelcomeActivity extends FragmentActivity {

  @Bind(R.id.welcomeTextView) TextView welcomeTextView;
  @Bind(R.id.contentPanel) ViewGroup contentPanel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (AssessmentHolder.getInstance().isAssessmentInProgress() || getIntent().getStringExtra("surveyName") != null) {
      LogUtils.d(getClass(), "In onCreate(), surveyName = " + getIntent().getStringExtra("surveyName"));
      startSurveyActivity(getIntent());
      finish();
    } else {
      initWelcomeScreen();
      scheduleAlarms();
    }
  }

  private void scheduleAlarms() {
    Task.callInBackground(new Callable<Void>() {
      @Override
      public Void call() throws Exception {
        new SurveyAlarmScheduler().scheduleAll(WelcomeActivity.this, getResources().openRawResource(R.raw.coop_alarm_schedule));
        return null;
      }
    });
  }

  private void startSurveyActivity(Intent surveyLaunchIntent) {
    startActivity(new Intent(this, SurveyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        .putExtras(surveyLaunchIntent));
  }

  private void startSurveyActivity(String surveyName) {
    startActivity(new Intent(this, SurveyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                                        .putExtra("surveyName", surveyName));
  }

  private void initWelcomeScreen() {
    setContentView(R.layout.survey_selection_screen);
    ButterKnife.bind(this);

    LayoutInflater inflater = LayoutInflater.from(this);
    final Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
    welcomeTextView.setTypeface(typeface);

    initStudyModel();

    WelcomeModel welcomeModel = AssessmentHolder.getInstance().getStudyModel().getWelcomeScreen();
    welcomeTextView.setText(Html.fromHtml(welcomeModel.getText()));

    for (final WelcomeLinkModel welcomeLinkModel : welcomeModel.getLinks()) {
      BootstrapButton button = (BootstrapButton) inflater.inflate(R.layout.survey_selection_button, null);
      button.setText(welcomeLinkModel.getSurveyName());
      button.setBootstrapType(welcomeLinkModel.getButtonType());
      button.setRightIcon(welcomeLinkModel.getIcon());

      button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          showAssessmentWelcomeScreen(welcomeLinkModel);
        }
      });
      contentPanel.addView(button);
    }
  }

  private void initStudyModel() {
    AssessmentParserService assessmentParserService = new AssessmentParserService();
    StudyModel studyModel = assessmentParserService.parseStudy(getResources().openRawResource(R.raw.coop_city));
    AssessmentHolder.getInstance().setStudyModel(studyModel);
  }

  private void showAssessmentWelcomeScreen(final WelcomeLinkModel welcomeLinkModel) {
    setContentView(R.layout.activity_welcome);
    TextView mainTextView = ButterKnife.findById(this, R.id.mainTextView);
    mainTextView.setText(welcomeLinkModel.getTransitionText());

    BootstrapButton previousButton = ButterKnife.findById(this, R.id.previousButton);
    if (welcomeLinkModel.getPreviousLabel() != null) {
      previousButton.setText(welcomeLinkModel.getPreviousLabel());
    }
    previousButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        initWelcomeScreen();
      }
    });

    BootstrapButton nextButton = ButterKnife.findById(this, R.id.nextButton);
    if (welcomeLinkModel.getNextLabel() != null) {
      nextButton.setText(welcomeLinkModel.getNextLabel());
    }
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startSurveyActivity(welcomeLinkModel.getSurveyName());
        finish();
      }
    });
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    setIntent(intent);

    String surveyName = intent.getStringExtra("surveyName");
    LogUtils.d(getClass(), "In WelcomeActivity.onNewIntent(), surveyName = " + surveyName);

    startSurveyActivity(intent);
    finish();
  }
}
