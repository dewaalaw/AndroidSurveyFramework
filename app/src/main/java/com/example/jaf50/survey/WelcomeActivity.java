package com.example.jaf50.survey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.WelcomeLinkModel;
import com.example.jaf50.survey.parser.WelcomeModel;
import com.example.jaf50.survey.service.AssessmentParserService;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class WelcomeActivity extends FragmentActivity {

  @InjectView(R.id.welcomeTextView)
  TextView welcomeTextView;

  @InjectView(R.id.contentPanel)
  ViewGroup contentPanel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (SurveyActivity.class.equals(SurveyApplication.getCurrentActivityClass())) {
      startSurveyActivity(null);
      finish();
    } else {
      initWelcomeScreen();
    }
  }

  private void startSurveyActivity(String surveyName) {
    Intent intent = new Intent(WelcomeActivity.this, SurveyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra("surveyName", surveyName);
    startActivity(intent);
  }

  private void initWelcomeScreen() {
    setContentView(R.layout.survey_selection_screen);
    ButterKnife.inject(this);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    StudyModel studyModel = assessmentParserService.parseStudy(getResources().openRawResource(R.raw.coop_city));
    AssessmentHolder.getInstance().setStudyModel(studyModel);

    LayoutInflater inflater = LayoutInflater.from(this);
    final Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
    welcomeTextView.setTypeface(typeface);

    WelcomeModel welcomeModel = studyModel.getWelcomeScreen();
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

  private void showAssessmentWelcomeScreen(final WelcomeLinkModel welcomeLinkModel) {
    setContentView(R.layout.activity_welcome);
    TextView mainTextView = ButterKnife.findById(this, R.id.mainTextView);
    mainTextView.setText(welcomeLinkModel.getTransitionText());

    BootstrapButton previousButton = ButterKnife.findById(this, R.id.previousButton);
    previousButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        initWelcomeScreen();
      }
    });

    BootstrapButton nextButton = ButterKnife.findById(this, R.id.nextButton);
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
    Log.d(getClass().getName(), "In onNewIntent().");
  }

  @Override
  protected void onResume() {
    super.onResume();
    SurveyApplication.setCurrentActivityClass(getClass());
  }
}
