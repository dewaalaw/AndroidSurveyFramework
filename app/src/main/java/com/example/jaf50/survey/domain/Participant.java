package com.example.jaf50.survey.domain;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.ArrayList;
import java.util.List;

public class Participant extends SugarRecord<Participant> {

  @Expose
  String assignedId;
  @Ignore
  List<Assessment> assessments = new ArrayList<>();

  public Participant() {
  }

  public String getAssignedId() {
    return assignedId;
  }

  public Participant setAssignedId(String assignedId) {
    this.assignedId = assignedId;
    return this;
  }

  public void eagerLoad(Survey survey) {
    this.assessments = loadAssessments(survey);
  }

  private List<Assessment> loadAssessments(Survey survey) {
    List<Assessment> assessments = Select.from(Assessment.class).where(
        Condition.prop("survey").eq(survey.getId() + "")).and(
        Condition.prop("participant").eq(getId() + "")).list();
    for (Assessment assessment : assessments) {
      assessment.eagerLoad();
    }
    return assessments;
  }

  public List<Assessment> getAssessments() {
    return assessments;
  }
}
