package com.askonthego.service;

import com.askonthego.parser.ComponentModel;
import com.askonthego.parser.ComponentModelDeserializer;
import com.askonthego.parser.StudyModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.io.InputStreamReader;

public class StudyParser {

  public StudyModel getStudy(InputStream inputStream) {
    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(ComponentModel.class, new ComponentModelDeserializer());
    Gson gson = gsonBuilder.create();

    return gson.fromJson(new InputStreamReader(inputStream), StudyModel.class);
  }
}
