package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.List;

public class Response {

  private List<String> values = new ArrayList<String>();

  public void addValue(String value) {
    values.add(value);
  }

  public List<String> getValues() {
    return values;
  }
}
