package com.example.jaf50.survey.domain;

import com.google.gson.annotations.Expose;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

public class Participant extends SugarRecord<Participant> {

  @Expose
  String assignedId;
  @Expose
  @Ignore
  List<Assessment> assessments = new ArrayList<>();

  public Participant() {
  }

  public String getAssignedId() {
    return assignedId;
  }

  public void setAssignedId(String assignedId) {
    this.assignedId = assignedId;
  }

  //  public List<Assessment> getAssessments() {
//    if (assessments == null || assessments.isEmpty()) {
//      assessments = Assessment.find(Assessment.class, "assessment = ?", getId() + "");
//    }
//    return assessments;
//  }

  public List<Assessment> getAssessments(Survey survey) {
    assessments = Assessment.find(Assessment.class, "survey = ? and participant = ? ", survey.getId() + "", getId() + "");
    return assessments;
  }
}
