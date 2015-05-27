package com.example.jaf50.survey.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ParseClassName("Assessment")
public class Assessment extends ParseObject {

  private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZZZ");

  public Assessment() {
  }

  public boolean isSynced() {
    return getBoolean("synced");
  }

  public void setSynced(boolean synced) {
    put("synced", synced);
  }

  public String getSurveyName() {
    return getString("surveyName");
  }

  public void setSurveyName(String surveyName) {
    put("surveyName", surveyName);
  }

  public void setResponses(AssessmentResponse... responses) {
    setResponses(Arrays.asList(responses));
  }

  public void setResponses(List<AssessmentResponse> responses) {
    addAll("responses", responses);
  }

  public List<AssessmentResponse> getResponses() {
    return getList("responses");
  }

  public ParseUser getParticipant() {
    return (ParseUser) get("participant");
  }

  public void setParticipant(ParseUser participant) {
    put("participant", participant);
  }

  public void setAssessmentStartDate(Date date) {
    put("assessmentStartDate", dateFormatter.format(date));
  }

  public String getAssessmentStartDate() {
    return getString("assessmentStartDate");
  }

  public void setAssessmentEndDate(Date date) {
    put("assessmentEndDate", dateFormatter.format(date));
  }

  public String getAssessmentEndDate() {
    return getString("assessmentEndDate");
  }

  public void setAssessmentTimeoutDate(Date date) {
    put("assessmentTimeoutDate", dateFormatter.format(date));
  }

  public String getAssessmentTimeoutDate() {
    return getString("assessmentTimeoutDate");
  }
}
