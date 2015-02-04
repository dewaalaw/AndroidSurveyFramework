package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CalendarView;

public class CalendarComponent extends CalendarView implements ISurveyComponent {

  public CalendarComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    return new Response();
  }

  @Override
  public View getView() {
    return this;
  }
}
