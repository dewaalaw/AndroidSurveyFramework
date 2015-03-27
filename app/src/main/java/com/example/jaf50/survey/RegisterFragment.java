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
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class RegisterFragment extends Fragment {

  @InjectView(R.id.registerButton)
  BootstrapButton registerButton;

  @InjectView(R.id.usernameTextBox)
  EditText userNameEditText;

  @InjectView(R.id.passwordTextBox)
  EditText passwordNameEditText;

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

        String username = userNameEditText.getText().toString();
        String password = passwordNameEditText.getText().toString();
        register(username, password);
      }
    });

    return view;
  }

  private void register(String username, String password) {
    ParseUser user = new ParseUser();
    user.setUsername(username);
    user.setPassword(password);

    user.signUpInBackground(new SignUpCallback() {
      public void done(ParseException e) {
        if (e == null) {
          // Hooray! Let them use the app now.
          registerationCallback.onRegisterSuccess();
        } else {
          // Sign up didn't succeed. Look at the ParseException to figure out what went wrong
          Toast.makeText(getActivity(), "Error during registration: " + e, Toast.LENGTH_LONG).show();
        }

        registerButton.setEnabled(true);
      }
    });
  }
}
