package com.example.jaf50.survey.parser;

import java.util.List;

public class ResponseModel {

  private String id;
  private List<Object> values;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<Object> getValues() {
    return values;
  }

  public void setValues(List<Object> values) {
    this.values = values;
  }
}
