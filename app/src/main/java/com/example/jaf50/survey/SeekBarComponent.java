package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SeekBarComponent extends DiscreteSeekBar implements ISurveyComponent {

  private static final int DEFAULT_PROGRESS = 50;

  private String responseId;

  private boolean valueSelected;

  public SeekBarComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
    setOnProgressChangeListener(new OnProgressChangeListener() {
      @Override
      public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
        if (fromUser) {
          valueSelected = true;
        }
      }
    });
    setProgress(DEFAULT_PROGRESS);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response(responseId);
    if (valueSelected) {
      response.addValue(getProgress() + "");
    }
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
