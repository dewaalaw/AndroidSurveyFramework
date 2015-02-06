package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.List;

public class Response {

  private List<Object> values = new ArrayList<Object>();

  public void addValue(Object value) {
    values.add(value);
  }

  public List<Object> getValues() {
    return values;
  }
}
