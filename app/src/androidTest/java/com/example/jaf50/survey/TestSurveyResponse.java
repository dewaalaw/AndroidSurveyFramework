package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.SurveyResponse;

public class TestSurveyResponse extends AndroidTestCase {

  public void test() {
    assertEquals(new SurveyResponse(), new SurveyResponse());
  }

  public void test2() {
    assertEquals(new SurveyResponse().setResponseId("one"), new SurveyResponse().setResponseId("one"));
  }

  public void test3() {
    assertEquals(new SurveyResponse().setResponseId("one").addValue("val1"), new SurveyResponse().setResponseId("one").addValue("val1"));
  }
}
