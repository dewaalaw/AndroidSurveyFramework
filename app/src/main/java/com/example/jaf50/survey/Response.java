package com.example.jaf50.survey;

import java.util.ArrayList;
import java.util.List;

public class Response {

  private String id;
  private List<Object> values = new ArrayList<Object>();

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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Response response = (Response) o;

    if (id != null ? !id.equals(response.id) : response.id != null) return false;
    if (values != null ? !values.equals(response.values) : response.values != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = id != null ? id.hashCode() : 0;
    result = 31 * result + (values != null ? values.hashCode() : 0);
    return result;
  }
}
