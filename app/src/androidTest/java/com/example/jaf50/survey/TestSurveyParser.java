package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import java.io.IOException;
import java.io.InputStream;

public class TestSurveyParser extends AndroidTestCase {

  public void test() throws IOException {
    InputStream surveyInputStream = getContext().getResources().openRawResource(R.raw.survey);
    SurveyParser parser = new SurveyParser();
    parser.parse(surveyInputStream);

    assertEquals("Description", parser.getSurveyDescription());
    assertEquals("My Survey", parser.getSurveyName());
  }
}
