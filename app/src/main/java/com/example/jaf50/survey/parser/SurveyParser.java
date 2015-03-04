package com.example.jaf50.survey.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class SurveyParser {

  public SurveyModel parse(InputStream inputStream) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(ComponentModel.class, new ComponentModelDeserializer());
    Gson gson = gsonBuilder.create();

    return gson.fromJson(new InputStreamReader(inputStream), SurveyModel.class);
  }
}
