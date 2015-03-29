package com.example.jaf50.survey.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Arrays;
import java.util.List;

@ParseClassName("Assessment")
public class Assessment extends ParseObject {

  public Assessment() {
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
}
