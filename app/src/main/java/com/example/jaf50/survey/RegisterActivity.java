package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.alarm.SurveyAlarmScheduler;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.service.LocalRegistrationService;
import com.example.jaf50.survey.service.OnlineRegistrationService;
import com.example.jaf50.survey.util.LogUtils;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterActivity extends FragmentActivity {

  @InjectView(R.id.actionButton)
  BootstrapButton actionButton;

  @InjectView(R.id.participantIdTextBox)
  EditText participantIdTextBox;

  @InjectView(R.id.passwordTextBox)
  EditText passwordTextBox;

  private OnlineRegistrationService onlineRegistrationService = new OnlineRegistrationService();
  private LocalRegistrationService localRegistrationService = new LocalRegistrationService();

  private static final boolean requiresOnlineRegistration = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
    setContentView(R.layout.activity_register);
    ButterKnife.inject(this);

    Participant participant = Participant.getActiveParticipant();
    if (participant != null) {
      new SurveyAlarmScheduler().scheduleAll(this, getResources().openRawResource(R.raw.coop_alarm_schedule));
      openSurveys();
    }

    actionButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String participantId = participantIdTextBox.getText().toString();
        String password = passwordTextBox.getText().toString();

        if (requiresOnlineRegistration) {
          registerOnline(participantId, password);
        } else {
          registerLocally(participantId, password);
        }
      }
    });
  }

  private void registerOnline(String participantId, String password) {
    actionButton.setEnabled(false);
    onlineRegistrationService.register(participantId, password, new JsonHttpResponseHandler() {
      @Override
      public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
        onRegisterSuccess();
        actionButton.setEnabled(true);
      }

      @Override
      public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
        Toast.makeText(RegisterActivity.this, "Error during registration: " + errorResponse, Toast.LENGTH_LONG).show();
        passwordTextBox.setText("");
        actionButton.setEnabled(true);
      }
    });
  }

  private void registerLocally(String participantId, String password) {
    actionButton.setEnabled(false);
    if (localRegistrationService.register(participantId, password)) {
      onRegisterSuccess();
      actionButton.setEnabled(true);
    } else {
      passwordTextBox.setText("");
      Toast.makeText(RegisterActivity.this, getString(R.string.registration_error_invalid_password), Toast.LENGTH_LONG).show();
      actionButton.setEnabled(true);
    }
  }

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
    Participant participant = Participant.getActiveParticipant();
    if (participant != null) {
      openSurveys();
    }
  }
}
