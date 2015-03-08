package com.example.jaf50.survey.domain;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class Assessment extends SugarRecord<Assessment> {

  String name;
  String description;
  @Ignore
  List<AssessmentResponse> responses;

  public Assessment() {
  }

  public List<AssessmentResponse> getResponses() {
    if (responses == null) {
      responses = AssessmentResponse.find(AssessmentResponse.class, "assessment = ?", getId() + "");
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

  public void setResponses(List<AssessmentResponse> responses) {
    this.responses = responses;
  }

  @Override
  public void save() {
    super.save();
    for (AssessmentResponse response : responses) {
      response.setAssessment(this);
      response.save();
    }
    super.save();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Assessment assessment = (Assessment) o;

    if (description != null ? !description.equals(assessment.description) : assessment.description != null)
      return false;
    if (name != null ? !name.equals(assessment.name) : assessment.name != null) return false;
    if (responses != null ? !responses.equals(assessment.responses) : assessment.responses != null)
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
