package com.example.jaf50.survey;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.service.RegistrationService;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterFragment extends Fragment {

  @InjectView(R.id.registerButton)
  BootstrapButton registerButton;

  @InjectView(R.id.participantIdTextBox)
  EditText participantIdTextBox;

  @InjectView(R.id.passwordTextBox)
  EditText passwordNameTextBox;

  private RegistrationService registrationService = new RegistrationService();

  public static interface RegisterationCallback {
    void onRegisterSuccess();
  }

  private RegisterationCallback registerationCallback;

  @Override
  public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (!(getActivity() instanceof RegisterationCallback)) {
      throw new IllegalStateException("Parent activity of the " + getClass().getSimpleName() + " needs to implement " + RegisterationCallback.class.getSimpleName());
    }

    this.registerationCallback = (RegisterationCallback) getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_register, container, false);
    ButterKnife.inject(this, view);

    registerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        registerButton.setEnabled(false);

        String participantId = participantIdTextBox.getText().toString();
        String password = passwordNameTextBox.getText().toString();
        registrationService.register(getActivity(), participantId, password, new JsonHttpResponseHandler() {
          @Override
          public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            registerationCallback.onRegisterSuccess();
            registerButton.setEnabled(true);
          }

          @Override
          public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
            Toast.makeText(getActivity(), "Error during registration: " + errorResponse, Toast.LENGTH_LONG).show();
            registerButton.setEnabled(true);
          }
        });
      }
    });

    return view;
  }
}
