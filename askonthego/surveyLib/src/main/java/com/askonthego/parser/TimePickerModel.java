package com.askonthego.parser;

import com.askonthego.ui.PickerStyle;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TimePickerModel extends ComponentModel {

  private String responseId;
  private PickerStyle pickerStyle = PickerStyle.CHOOSER;
  private String label;
}
