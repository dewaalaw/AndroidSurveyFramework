package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;

import com.example.jaf50.survey.response.Response;

import java.util.ArrayList;
import java.util.List;

public class CheckboxGroupComponent extends LinearLayout implements ISurveyComponent {

  private String responseId;

  private List<CheckBox> checkboxes = new ArrayList<CheckBox>();

  public CheckboxGroupComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewGroup getView() {
    return this;
  }

  public void addComponent(CheckBox checkBox) {
    addView(checkBox);
    checkboxes.add(checkBox);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  public Response getResponse() {
    Response response = new Response(responseId);
    for (CheckBox checkbox : checkboxes) {
      if (checkbox.isChecked()) {
        response.addValue(checkbox.getText().toString());
      }
    }
    return response;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }
}
