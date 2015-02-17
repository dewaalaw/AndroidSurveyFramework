package com.example.jaf50.survey.response;

import java.util.ArrayList;
import java.util.List;

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

  @Override
  public String toString() {
    return "Response{" +
        "id='" + id + '\'' +
        ", values=" + values +
        '}';
  }
}
