package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CheckboxComponent extends CheckBox {

  private String value;

  public CheckboxComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }
}
