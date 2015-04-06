package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.parse.ParseUser;

public class RegisterActivity extends FragmentActivity implements RegisterFragment.RegisterationCallback {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      openSurveys();
    }
  }

  @Override
  public void onRegisterSuccess() {
    openSurveys();
  }

  private void openSurveys() {
    Intent surveyIntent = new Intent(this, WelcomeActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
    if (getIntent() != null) {
      surveyIntent.putExtra("surveyName", getIntent().getStringExtra("surveyName"));
    }
    Log.d(getClass().getName(), "In openSurveys(), surveyName = " + getIntent().getStringExtra("surveyName"));
    startActivity(surveyIntent);
    finish();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    Log.d(getClass().getName(), "In onNewIntent().");
    setIntent(intent);
    ParseUser currentUser = ParseUser.getCurrentUser();
    if (currentUser != null) {
      openSurveys();
    }
  }
}
