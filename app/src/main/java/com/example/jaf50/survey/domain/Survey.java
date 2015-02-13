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
}
