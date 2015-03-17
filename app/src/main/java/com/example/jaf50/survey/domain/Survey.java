package com.example.jaf50.survey.domain;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;

public class Survey extends SugarRecord<Survey> {

  @Expose
  String name;

  public String getName() {
    return name;
  }

  public Survey setName(String name) {
    this.name = name;
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Survey survey = (Survey) o;

    if (name != null ? !name.equals(survey.name) : survey.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return name != null ? name.hashCode() : 0;
  }

  @Override
  public String toString() {
    return "Survey{" +
        "name='" + name + '\'' +
        '}';
  }
}
