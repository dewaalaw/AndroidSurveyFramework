package com.example.jaf50.survey.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import lombok.Getter;
import lombok.Setter;

public class RadioButtonComponent extends RadioButton {

  @Getter @Setter private String value;

  public RadioButtonComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
}
