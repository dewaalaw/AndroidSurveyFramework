package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

public class RadioButtonComponent extends RadioButton {

  private String value;

  public RadioButtonComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
