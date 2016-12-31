package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.askonthego.SurveyApplication;
import com.askonthego.domain.AssessmentResponse;

public class TestAssessmentResponse extends AndroidTestCase {

  @Override
  protected void setUp() throws Exception {
    //SurveyApplication.registerParseClasses();
  }

  public void test() {
    assertTrue(new AssessmentResponse().equalsResponse(new AssessmentResponse()));
  }

  public void test2() {
    AssessmentResponse response1 = new AssessmentResponse();
    response1.setResponseId("one");

    AssessmentResponse response2 = new AssessmentResponse();
    response2.setResponseId("one");

    assertTrue(response1.equalsResponse(response2));
  }

  public void test3() {
    AssessmentResponse response1 = new AssessmentResponse();
    response1.setResponseId("one");
    response1.addValue("val1");

    AssessmentResponse response2 = new AssessmentResponse();
    response2.setResponseId("one");
    response2.addValue("val1");

    assertTrue(response1.equalsResponse(response2));
  }
}
