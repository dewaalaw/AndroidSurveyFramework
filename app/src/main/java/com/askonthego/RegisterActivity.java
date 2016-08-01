package com.askonthego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.askonthego.domain.Participant;
import com.askonthego.service.LocalRegistrationService;
import com.askonthego.service.OnlineRegistrationService;
import com.askonthego.service.Preferences;
import com.askonthego.service.Token;
import com.askonthego.util.LogUtils;
import com.beardedhen.androidbootstrap.BootstrapButton;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.pristine.sheath.Sheath;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RegisterActivity extends FragmentActivity {

    @BindView(R.id.actionButton) BootstrapButton actionButton;
    @BindView(R.id.participantIdTextBox) EditText participantIdTextBox;
    @BindView(R.id.passwordTextBox) EditText passwordTextBox;

    @Inject OnlineRegistrationService onlineRegistrationService;
    @Inject LocalRegistrationService localRegistrationService;
    @Inject Preferences preferences;

    private static final boolean requiresOnlineRegistration = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sheath.inject(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

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
        onlineRegistrationService.register(participantId, password, new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                preferences.saveApiToken(token.getToken());
                openSurveys();
                actionButton.setEnabled(true);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(RegisterActivity.this, getString(R.string.registration_error), Toast.LENGTH_LONG).show();
                LogUtils.e(getClass(), "Registration error", error);
                passwordTextBox.setText("");
                actionButton.setEnabled(true);
            }
        });
    }

    private void registerLocally(String participantId, String password) {
        actionButton.setEnabled(false);
        if (localRegistrationService.register(participantId, password)) {
            openSurveys();
            actionButton.setEnabled(true);
        } else {
            passwordTextBox.setText("");
            Toast.makeText(RegisterActivity.this, getString(R.string.registration_error_invalid_password), Toast.LENGTH_LONG).show();
            actionButton.setEnabled(true);
        }
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        Participant participant = Participant.getActiveParticipant();
        if (participant != null) {
            openSurveys();
        }
    }
}
