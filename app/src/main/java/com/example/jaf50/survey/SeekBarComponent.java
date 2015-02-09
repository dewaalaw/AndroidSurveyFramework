package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SeekBarComponent extends DiscreteSeekBar implements ISurveyComponent {

  private String responseId;

  public SeekBarComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response(responseId);
    response.addValue(getProgress() + "");
    return response;
  }

  @Override
  public View getView() {
    return this;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }
}
