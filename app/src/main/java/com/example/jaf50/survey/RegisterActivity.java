package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.jaf50.survey.alarm.SurveyAlarmScheduler;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.util.LogUtils;

public class RegisterActivity extends FragmentActivity implements RegisterFragment.RegisterationCallback {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_register);

    Participant participant = Participant.getCurrentParticipant(this);
    if (participant != null) {
      new SurveyAlarmScheduler().scheduleAll(this, getResources().openRawResource(R.raw.coop_alarm_schedule));
      openSurveys();
    }
  }

  @Override
  public void onRegisterSuccess() {
    new SurveyAlarmScheduler().scheduleAll(this, getResources().openRawResource(R.raw.coop_alarm_schedule));
    openSurveys();
  }

  private void openSurveys() {
    Intent surveyIntent = new Intent(this, WelcomeActivity.class)
        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
        .putExtras(getIntent());

    LogUtils.d(getClass(), "In openSurveys(), surveyName = " + getIntent().getStringExtra("surveyName"));

    startActivity(surveyIntent);
    finish();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    LogUtils.d(getClass(), "In onNewIntent().");
    setIntent(intent);
    Participant participant = Participant.getCurrentParticipant(this);
    if (participant != null) {
      openSurveys();
    }
  }
}
