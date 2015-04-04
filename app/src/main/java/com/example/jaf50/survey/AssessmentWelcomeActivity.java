package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;

import butterknife.ButterKnife;

public class AssessmentWelcomeActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_welcome);

    if (getIntent() != null) {
      String welcomeText = getIntent().getStringExtra("welcomeText");
      TextView mainTextView = ButterKnife.findById(this, R.id.mainTextView);
      mainTextView.setText(welcomeText);
    }

    BootstrapButton previousButton = ButterKnife.findById(this, R.id.previousButton);
    previousButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });

    BootstrapButton nextButton = ButterKnife.findById(this, R.id.nextButton);
    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(AssessmentWelcomeActivity.this, SurveyActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if (getIntent() != null) {
          intent.putExtra("surveyName", getIntent().getStringExtra("surveyName"));
        }
        startActivity(intent);
        finish();
      }
    });
  }
}
