package com.example.jaf50.survey.parser;

import java.util.List;

public class CheckboxGroupModel extends ComponentModel {

  private String responseId;
  private List<InputModel> inputs;

  public String getResponseId() {
    return responseId;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public List<InputModel> getInputs() {
    return inputs;
  }

  public void setInputs(List<InputModel> inputs) {
    this.inputs = inputs;
  }
}
