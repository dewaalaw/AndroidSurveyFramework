package com.example.jaf50.survey.parse.sdk;

import com.parse.FindCallback;
import com.parse.ParseException;

import java.util.List;

public abstract class BetterFindCallback<T extends com.parse.ParseObject> implements FindCallback<T> {

  @Override
  public void done(List<T> results, ParseException e) {
    if (e != null) {
      onFailure(e);
    } else {
      onSuccess(results);
    }
  }

  public abstract void onSuccess(List<T> results);

  protected abstract void onFailure(ParseException e);
}
