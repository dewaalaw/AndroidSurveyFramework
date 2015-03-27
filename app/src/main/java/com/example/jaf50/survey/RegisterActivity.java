package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
    Intent surveyIntent = new Intent(this, SurveyActivity.class);
    startActivity(surveyIntent);
  }
}
