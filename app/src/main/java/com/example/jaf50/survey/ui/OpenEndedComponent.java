package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jaf50.survey.response.Response;

import lombok.Getter;
import lombok.Setter;

public class OpenEndedComponent extends BootstrapEditText implements ISurveyComponent {

  @Getter @Setter private String responseId;

  public OpenEndedComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  /*
   * From http://stackoverflow.com/questions/5014219/multiline-edittext-with-done-softinput-action-label-on-2-3/12570003#12570003
   */
  @Override
  public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    InputConnection connection = super.onCreateInputConnection(outAttrs);
    int imeActions = outAttrs.imeOptions&EditorInfo.IME_MASK_ACTION;
    if ((imeActions&EditorInfo.IME_ACTION_DONE) != 0) {
      // clear the existing action
      outAttrs.imeOptions ^= imeActions;
      // set the DONE action
      outAttrs.imeOptions |= EditorInfo.IME_ACTION_DONE;
    }
    if ((outAttrs.imeOptions&EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0) {
      outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
    }
    return connection;
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    setGravity(Gravity.CENTER);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response(responseId);
    if (!getText().toString().trim().isEmpty()) {
      response.addValue(getText().toString().trim());
    }
    return response;
  }

  @Override
  public View getView() {
    return this;
  }
}
