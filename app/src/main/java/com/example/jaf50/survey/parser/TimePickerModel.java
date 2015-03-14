package com.example.jaf50.survey.parser;

import com.example.jaf50.survey.ui.PickerStyle;

public class TimePickerModel extends ComponentModel {

  private String responseId;
  private PickerStyle pickerStyle = PickerStyle.CHOOSER;
  private String label;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public PickerStyle getPickerStyle() {
    return pickerStyle;
  }

  public void setPickerStyle(PickerStyle pickerStyle) {
    this.pickerStyle = pickerStyle;
  }

  public String getLabel() {
    return label;
  }

  public void setLabel(String label) {
    this.label = label;
  }
}
