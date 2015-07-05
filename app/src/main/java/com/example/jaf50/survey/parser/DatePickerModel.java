package com.example.jaf50.survey.parser;

import com.example.jaf50.survey.ui.PickerStyle;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class DatePickerModel extends ComponentModel {

  private String responseId;
  private PickerStyle pickerStyle = PickerStyle.CHOOSER;
  private String label;
}
