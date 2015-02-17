package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.jaf50.survey.response.Response;

public class RadioGroupComponent extends RadioGroup implements ISurveyComponent {

  private String responseId;

  public RadioGroupComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public ViewGroup getView() {
    return this;
  }

  public void addComponent(RadioButton radioButton) {
    addView(radioButton);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  public Response getResponse() {
    Response response = new Response(responseId);
    int checkedId = getCheckedRadioButtonId();
    if (checkedId != -1) {
      RadioButton radioButton = (RadioButton) findViewById(checkedId);
      response.addValue(radioButton.getText().toString());
    }
    return response;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }
}
