package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.jaf50.survey.response.Response;

import java.util.ArrayList;
import java.util.List;

public class CheckboxGroupComponent extends LinearLayout implements ISurveyComponent {

  private String responseId;

  private List<CheckboxComponent> checkboxComponents = new ArrayList<CheckboxComponent>();

  public CheckboxGroupComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewGroup getView() {
    return this;
  }

  public void addComponent(CheckboxComponent checkboxComponent) {
    addView(checkboxComponent);
    checkboxComponents.add(checkboxComponent);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  public Response getResponse() {
    Response response = new Response(responseId);
    for (CheckboxComponent checkboxComponent : checkboxComponents) {
      if (checkboxComponent.isChecked()) {
        response.addValue(checkboxComponent.getValue());
      }
    }
    return response;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }
}
