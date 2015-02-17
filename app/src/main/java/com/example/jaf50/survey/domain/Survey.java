package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class Survey extends SugarRecord<Survey> {

  String name;
  String description;
  @Ignore
  List<SurveyResponse> responses;

  public Survey() {
  }

  public List<SurveyResponse> getResponses() {
    if (responses == null) {
      responses = SurveyResponse.find(SurveyResponse.class, "survey = ?", getId() + "");
    }
    return responses;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setResponses(List<SurveyResponse> responses) {
    this.responses = responses;
  }

  @Override
  public void save() {
    super.save();
    for (SurveyResponse response : responses) {
      response.setSurvey(this);
      response.save();
    }
    super.save();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Survey survey = (Survey) o;

    if (description != null ? !description.equals(survey.description) : survey.description != null)
      return false;
    if (name != null ? !name.equals(survey.name) : survey.name != null) return false;
    if (responses != null ? !responses.equals(survey.responses) : survey.responses != null)
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = name != null ? name.hashCode() : 0;
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (responses != null ? responses.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Survey{" +
        "name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", responses=" + responses +
        '}';
  }
}
