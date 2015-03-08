package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.AssessmentResponse;

public class TestAssessmentResponse extends AndroidTestCase {

  public void test() {
    assertEquals(new AssessmentResponse(), new AssessmentResponse());
  }

  public void test2() {
    assertEquals(new AssessmentResponse().setResponseId("one"), new AssessmentResponse().setResponseId("one"));
  }

  public void test3() {
    assertEquals(new AssessmentResponse().setResponseId("one").addValue("val1"), new AssessmentResponse().setResponseId("one").addValue("val1"));
  }
}
