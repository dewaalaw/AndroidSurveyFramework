package com.example.jaf50.survey.response;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class Response {

  private String id;
  private List<Object> values = new ArrayList<Object>();

  public Response() {
  }

  public Response(String id) {
    this.id = id;
  }

  public String getId() {
    return id;
  }

  public Response addValue(Object value) {
    values.add(value);
    return this;
  }

  public List<Object> getValues() {
    return values;
  }

  public boolean isEmpty() {
    return values.isEmpty();
  }

  public boolean contains(Response otherResponse) {
    if (otherResponse == null) {
      return false;
    }
    return values.containsAll(otherResponse.getValues());
  }
}
