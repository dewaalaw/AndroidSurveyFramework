package com.example.jaf50.survey.parse.sdk;

import com.parse.ParseException;
import com.parse.SaveCallback;

public abstract class BetterSaveCallback implements SaveCallback {

  @Override
  public void done(ParseException e) {
    if (e != null) {
      onFailure(e);
    } else {
      onSuccess();
    }
  }

  protected abstract void onFailure(ParseException e);

  protected abstract void onSuccess();
}
