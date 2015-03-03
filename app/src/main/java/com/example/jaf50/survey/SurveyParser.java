package com.example.jaf50.survey;

import com.example.jaf50.survey.domain.Survey;
import com.google.gson.stream.JsonReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class SurveyParser {

  private String surveyDescription;
  private String surveyName;

  public void parse(InputStream inputStream) throws IOException {
    JsonReader reader = new JsonReader(new InputStreamReader(inputStream));
    reader.beginObject();
    while(reader.hasNext()) {
      String name = reader.nextName();
      if (name.equals("description")) {
        this.surveyDescription = reader.nextString();
      } else if (name.equals("name")) {
        this.surveyName = reader.nextString();
      } else if (name.equals("screens")) {
        parseScreens(reader);
      } else {
        reader.skipValue();
      }
    }
    reader.endObject();
  }

  private void parseScreens(JsonReader reader) throws IOException {
    reader.beginArray();
    while(reader.hasNext()) {
      reader.beginObject();
      while(reader.hasNext()) {
        String name = reader.nextName();
        if (name.equals("id")) {
          // TODO - read the screen id. Need to create a model object to hold the survey screen independently of
          // the screen view object.
          reader.nextString();
        } else {
          reader.skipValue();
        }
      }
      reader.endObject();
    }
    reader.endArray();
  }

  public String getSurveyDescription() {
    return surveyDescription;
  }

  public String getSurveyName() {
    return surveyName;
  }

  public Survey getSurvey() {
    return null;
  }

  public List<SurveyScreen> getSurveyScreens() {
    return null;
  }
}
