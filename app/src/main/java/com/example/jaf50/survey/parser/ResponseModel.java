package com.example.jaf50.survey.parser;

import java.util.List;

public class ResponseModel extends ComponentModel {

  private String id;
  private List<String> values;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }
}
