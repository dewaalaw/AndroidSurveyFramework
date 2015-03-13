package com.example.jaf50.survey.parser;

import com.example.jaf50.survey.ui.DatePickerStyle;

public class DatePickerModel extends ComponentModel {

  private String responseId;
  private DatePickerStyle pickerStyle = DatePickerStyle.CALENDAR;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public DatePickerStyle getPickerStyle() {
    return pickerStyle;
  }

  public void setPickerStyle(DatePickerStyle pickerStyle) {
    this.pickerStyle = pickerStyle;
  }
}
