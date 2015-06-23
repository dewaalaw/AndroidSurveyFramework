package com.example.jaf50.survey.parser;

import java.util.List;

public class CheckboxGroupModel extends ComponentModel {

  private String responseId;
  private List<CheckboxInputModel> inputs;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public List<CheckboxInputModel> getInputs() {
    return inputs;
  }

  public void setInputs(List<CheckboxInputModel> inputs) {
    this.inputs = inputs;
  }
}
