package com.example.jaf50.survey.domain;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Assessment extends SugarRecord<Assessment> {

  @Expose
  Survey survey;
  @Expose
  Participant participant;
  @Expose
  @Ignore
  List<AssessmentResponse> responses = new ArrayList<>();

  String description;
  boolean submitted;

  public Assessment() {
  }

  public void eagerLoad() {
    this.responses = loadResponses();
  }

  private List<AssessmentResponse> loadResponses() {
    List<AssessmentResponse> assessmentResponses = AssessmentResponse.find(AssessmentResponse.class, "assessment = ?", getId() + "");
    for (AssessmentResponse assessmentResponse : assessmentResponses) {
      assessmentResponse.eagerLoad();
    }
    return assessmentResponses;
  }

  public Survey getSurvey() {
    return survey;
  }

  public Assessment setSurvey(Survey survey) {
    this.survey = survey;
    return this;
  }

  public String getDescription() {
    return description;
  }

  public Assessment setDescription(String description) {
    this.description = description;
    return this;
  }

  public Assessment setResponses(AssessmentResponse... responses) {
    return setResponses(Arrays.asList(responses));
  }

  public Assessment setResponses(List<AssessmentResponse> responses) {
    this.responses = responses;
    return this;
  }

  public List<AssessmentResponse> getResponses() {
    return responses;
  }

  public Participant getParticipant() {
    return participant;
  }

  public Assessment setParticipant(Participant participant) {
    this.participant = participant;
    return this;
  }

  public boolean isSubmitted() {
    return submitted;
  }

  public Assessment setSubmitted(boolean submitted) {
    this.submitted = submitted;
    return this;
  }

  @Override
  public void save() {
    if (survey == null) {
      throw new DomainException(getClass() + " 'survey' property cannot be null.");
    }
    if (participant == null) {
      throw new DomainException(getClass() + " 'participant' property cannot be null.");
    }

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
    if (participant != null ? !participant.equals(that.participant) : that.participant != null)
      return false;
    if (responses != null ? !responses.equals(that.responses) : that.responses != null)
      return false;
    if (survey != null ? !survey.equals(that.survey) : that.survey != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = survey != null ? survey.hashCode() : 0;
    result = 31 * result + (participant != null ? participant.hashCode() : 0);
    result = 31 * result + (responses != null ? responses.hashCode() : 0);
    result = 31 * result + (description != null ? description.hashCode() : 0);
    result = 31 * result + (submitted ? 1 : 0);
    return result;
  }

  @Override
  public String toString() {
    return "Assessment{" +
        "survey=" + survey +
        ", participant=" + participant +
        ", responses=" + responses +
        ", description='" + description + '\'' +
        ", submitted=" + submitted +
        '}';
  }
}
