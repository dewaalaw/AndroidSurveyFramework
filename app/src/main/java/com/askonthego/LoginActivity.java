package com.askonthego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.askonthego.alarm.TimeoutEvent;
import com.askonthego.domain.Participant;
import com.askonthego.http.Error;
import com.askonthego.http.RestError;
import com.askonthego.service.Credentials;
import com.askonthego.service.ParticipantDAO;
import com.askonthego.service.Preferences;
import com.askonthego.service.RegistrationService;
import com.askonthego.service.Token;
import com.askonthego.util.LogUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.pristine.sheath.Sheath;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class LoginActivity extends FragmentActivity {

    @BindView(R.id.loginButton) BootstrapButton loginButton;
    @BindView(R.id.registerButton) BootstrapButton registerButton;
    @BindView(R.id.participantIdTextBox) EditText participantIdTextBox;
    @BindView(R.id.passwordTextBox) EditText passwordTextBox;

    @Inject RegistrationService registrationService;
    @Inject ParticipantDAO participantDAO;
    @Inject Preferences preferences;

    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String participantId = participantIdTextBox.getText().toString();
            String password = passwordTextBox.getText().toString();

            if (participantId.trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.participant_id_empty_error), Toast.LENGTH_LONG).show();
            } else if (password.trim().isEmpty()) {
                Toast.makeText(LoginActivity.this, getString(R.string.password_empty_error), Toast.LENGTH_LONG).show();
            } else {
                authenticate(participantId, password);
            }
        }
    };

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sheath.inject(this);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
    }

    /**
     * Enable or disable the button click listeners. This is done rather than simply disabling the
     * buttons because disabling the buttons looks ugly.
     */
    private void enableClickListeners(boolean enable) {
        loginButton.setOnClickListener(enable ? loginClickListener : null);
        registerButton.setOnClickListener(enable ? registerClickListener : null);
    }

    private void authenticate(String participantId, String password) {
        enableClickListeners(false);

        registrationService.authenticate(new Credentials(participantId, password), new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                preferences.saveApiToken(token.getToken());
                openSurveys();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 503) {
                    Toast.makeText(LoginActivity.this, getString(R.string.service_unavailable), Toast.LENGTH_LONG).show();
                } else {
                    RestError restError = (RestError) error.getBodyAs(RestError.class);
                    if (restError.getValidation() != null && !restError.getValidation().getErrors().isEmpty()) {
                        Error validationError = restError.getValidation().getErrors().get(0);
                        Toast.makeText(LoginActivity.this, validationError.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
                    }
                }
                enableClickListeners(true);
            }
        });
    }

    private void openSurveys() {
        Intent surveyIntent = new Intent(this, WelcomeActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(getIntent());

        if (wasLaunchedFromRecents()) {
            surveyIntent.removeExtra("timeoutEvent");
            surveyIntent.removeExtra("alarmEvent");
            surveyIntent.removeExtra("surveyName");
        }

        if (!surveyIntent.hasExtra("timeoutEvent") && !surveyIntent.hasExtra("alarmEvent")) {
            surveyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        LogUtils.d(getClass(), "In openSurveys(), surveyName = " + getIntent().getStringExtra("surveyName"));

        startActivity(surveyIntent);
        finish();
    }

    private boolean wasLaunchedFromRecents() {
        return (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.d(getClass(), "In onNewIntent().");
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loginButton.setOnClickListener(loginClickListener);
        registerButton.setOnClickListener(registerClickListener);
        Participant participant = participantDAO.getActiveParticipant();
        if (participant != null) {
            openSurveys();
        }
    }
}
