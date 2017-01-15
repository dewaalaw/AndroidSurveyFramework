package com.askonthego;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.askonthego.domain.Participant;
import com.askonthego.service.Credentials;
import com.askonthego.http.Error;
import com.askonthego.service.RegistrationService;
import com.askonthego.service.ParticipantDAO;
import com.askonthego.service.Preferences;
import com.askonthego.http.RestError;
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

public class RegisterActivity extends FragmentActivity {

    @BindView(R.id.registerButton) BootstrapButton registerButton;
    @BindView(R.id.participantIdTextBox) EditText participantIdTextBox;
    @BindView(R.id.passwordTextBox) EditText passwordTextBox;
    @BindView(R.id.passwordConfirmTextBox) EditText passwordConfirmTextBox;

    @Inject RegistrationService registrationService;
    @Inject ParticipantDAO participantDAO;
    @Inject Preferences preferences;

    private View.OnClickListener registerClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String participantId = participantIdTextBox.getText().toString();
            String password = passwordTextBox.getText().toString();
            String passwordConfirm = passwordConfirmTextBox.getText().toString();
            if (password.isEmpty() && passwordConfirm.isEmpty()) {
                Toast.makeText(RegisterActivity.this, getString(R.string.password_empty_error), Toast.LENGTH_LONG).show();
            } else if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, getString(R.string.password_match_error), Toast.LENGTH_LONG).show();
            } else {
                register(participantId, password);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sheath.inject(this);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    private void register(String participantId, String password) {
        enableClickListener(false);

        registrationService.register(new Credentials(participantId, password), new Callback<Token>() {
            @Override
            public void success(Token token, Response response) {
                preferences.saveApiToken(token.getToken());
                openSurveys();
                Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getResponse().getStatus() == 503) {
                    Toast.makeText(RegisterActivity.this, getString(R.string.service_unavailable), Toast.LENGTH_LONG).show();
                } else {
                    RestError restError = (RestError) error.getBodyAs(RestError.class);
                    if (restError.getValidation() != null && !restError.getValidation().getErrors().isEmpty()) {
                        Error validationError = restError.getValidation().getErrors().get(0);
                        Toast.makeText(RegisterActivity.this, validationError.getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_error), Toast.LENGTH_LONG).show();
                    }
                }
                enableClickListener(true);
            }
        });
    }

    private void openSurveys() {
        Intent surveyIntent = new Intent(this, WelcomeActivity.class)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK)
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
        enableClickListener(true);
        Participant participant = participantDAO.getActiveParticipant();
        if (participant != null) {
            openSurveys();
        }
    }

    private void enableClickListener(boolean enable) {
        registerButton.setOnClickListener(enable ? registerClickListener : null);
    }
}
