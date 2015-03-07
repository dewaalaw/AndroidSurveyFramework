package com.example.jaf50.survey.parser;

public class SliderModel extends ComponentModel {

  private String responseId;
  private String leftLabel;
  private String rightLabel;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getLeftLabel() {
    return leftLabel;
  }

  public void setLeftLabel(String leftLabel) {
    this.leftLabel = leftLabel;
  }

  public String getRightLabel() {
    return rightLabel;
  }

  public void setRightLabel(String rightLabel) {
    this.rightLabel = rightLabel;
  }
}
