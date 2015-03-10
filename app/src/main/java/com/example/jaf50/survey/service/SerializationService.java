package com.example.jaf50.survey.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SerializationService {

  private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public String serialize(Object object) {
    Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setDateFormat(DATE_FORMAT).create();
    return gson.toJson(object);
  }
}
