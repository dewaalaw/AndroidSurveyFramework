package com.example.jaf50.survey.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.util.List;

@ParseClassName("Participant")
public class Participant extends ParseObject {

  public String getAssignedId() {
    return getString("assignedId");
  }

  public void setAssignedId(String assignedId) {
    put("assignedId", assignedId);
  }

  public List<Assessment> getAssessments() {
    return getList("assessments");
  }

  public void setAssessments(List<Assessment> assessments) {
    addAll("assessments", assessments);
  }

//  private List<Assessment> loadAssessments(Survey survey) {
//    List<Assessment> assessments = Select.from(Assessment.class).where(
//        Condition.prop("survey").eq(survey.getId() + "")).and(
//        Condition.prop("participant").eq(getId() + "")).list();
//    for (Assessment assessment : assessments) {
//      assessment.eagerLoad();
//    }
//    return assessments;
//  }
}
