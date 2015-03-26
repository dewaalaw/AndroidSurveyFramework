package com.example.jaf50.survey.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Survey")
public class Survey extends ParseObject {

  public String getName() {
    return getString("name");
  }

  public void setName(String name) {
    put("name", name);
  }
}
