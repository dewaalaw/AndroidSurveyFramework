package com.example.jaf50.survey;

import android.view.View;

public interface ISurveyComponent {

  boolean acceptsResponse();

  Response getResponse();

  View getView();
}
