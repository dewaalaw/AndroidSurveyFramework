package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

public class SeekBarComponent extends DiscreteSeekBar implements ISurveyComponent {

  public SeekBarComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response();
    response.addValue(getProgress() + "");
    return response;
  }

  @Override
  public View getView() {
    return this;
  }
}
