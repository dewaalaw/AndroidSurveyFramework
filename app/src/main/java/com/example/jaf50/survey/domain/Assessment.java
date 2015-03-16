package com.example.jaf50.survey.domain;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.List;

public class Assessment extends SugarRecord<Assessment> {

  @Expose
  Survey survey;
  @Ignore
  Participant participant;
  @Expose
  @Ignore
  List<AssessmentResponse> responses;

  String description;
  boolean submitted;

  public Assessment() {
  }

  public List<AssessmentResponse> getResponses() {
    if (responses == null) {
      responses = AssessmentResponse.find(AssessmentResponse.class, "assessment = ?", getId() + "");
    }
    return responses;
  }

  public Survey getSurvey() {
    return survey;
  }

  public void setSurvey(Survey survey) {
    this.survey = survey;
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

  public boolean isSubmitted() {
    return submitted;
  }

  public void setSubmitted(boolean submitted) {
    this.submitted = submitted;
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

    Assessment that = (Assessment) o;

    if (submitted != that.submitted) return false;
    if (description != null ? !description.equals(that.description) : that.description != null)
      return false;
    if (responses != null ? !responses.equals(that.responses) : that.responses != null)
      return false;
    if (survey != null ? !survey.equals(that.survey) : that.survey != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = survey != null ? survey.hashCode() : 0;
    result = 31 * result + (responses != null ? responses.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (submitted ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Assessment{" +
        "survey=" + survey +
        ", responses=" + responses +
        ", description='" + description + '\'' +
        ", submitted=" + submitted +
        '}';
  }
}
