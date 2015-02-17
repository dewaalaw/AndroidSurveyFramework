package com.example.jaf50.survey.ui;

import android.view.View;

import com.example.jaf50.survey.response.Response;

public interface ISurveyComponent {

  boolean acceptsResponse();

  Response getResponse();

  View getView();
}
