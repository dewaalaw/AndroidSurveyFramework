package com.example.jaf50.survey.service;

import com.example.jaf50.survey.parser.ComponentModel;
import com.example.jaf50.survey.parser.ComponentModelDeserializer;
import com.example.jaf50.survey.parser.SurveyModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class AssessmentParserService {

  public SurveyModel parse(InputStream inputStream) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(ComponentModel.class, new ComponentModelDeserializer());
    Gson gson = gsonBuilder.create();

    return gson.fromJson(new InputStreamReader(inputStream), SurveyModel.class);
  }
}
