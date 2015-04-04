package com.example.jaf50.survey;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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
    setContentView(R.layout.survey_selection_screen);
    ButterKnife.inject(this);

    Toast.makeText(this, "create", Toast.LENGTH_SHORT).show();

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    StudyModel studyModel = assessmentParserService.parseStudy(getResources().openRawResource(R.raw.coop_city));
    AssessmentHolder.getInstance().setStudyModel(studyModel);

    LayoutInflater inflater = LayoutInflater.from(this);
    final Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
    welcomeTextView.setTypeface(typeface);

    WelcomeModel welcomeModel = studyModel.getWelcomeScreen();
    for (final WelcomeLinkModel welcomeLinkModel : welcomeModel.getLinks()) {
      BootstrapButton button = (BootstrapButton) inflater.inflate(R.layout.survey_selection_button, null);
      button.setText(welcomeLinkModel.getSurveyName());
      button.setBootstrapType(welcomeLinkModel.getButtonType());
      button.setRightIcon(welcomeLinkModel.getIcon());

      button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          Intent intent = new Intent(WelcomeActivity.this, AssessmentWelcomeActivity.class)
              .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              .putExtra("welcomeText", welcomeLinkModel.getTransitionText())
              .putExtra("surveyName", welcomeLinkModel.getSurveyName());
          startActivity(intent);
        }
      });
      contentPanel.addView(button);
    }
  }
}
