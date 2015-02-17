package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.example.jaf50.survey.response.Response;

public class TextComponent extends TextView implements ISurveyComponent {

  public TextComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return false;
  }

  @Override
  public Response getResponse() {
    return null;
  }

  @Override
  public View getView() {
    return this;
  }
}
