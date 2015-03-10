package com.example.jaf50.survey.service;

import com.example.jaf50.survey.domain.Assessment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class AssessmentSerializationService {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public String serialize(Assessment assessment) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(DATE_FORMAT).create();
    return gson.toJson(assessment);
  }
}
