package com.askonthego.ui;

import android.view.View;

import com.askonthego.response.Response;

public interface ISurveyComponent {

    boolean acceptsResponse();

    Response getResponse();

    View getView();
}
