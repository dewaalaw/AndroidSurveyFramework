package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.ResponseConditionOperator;
import com.example.jaf50.survey.response.ResponseCondition;
import com.example.jaf50.survey.response.ResponseCriteria;

import java.util.Arrays;

public class TestResponseCriteria extends AndroidTestCase {

  ResponseCriteria criteria;

  @Override
  protected void setUp() throws Exception {
    criteria = new ResponseCriteria();
    SurveyApplication.registerParseClasses();
  }

  public void test_EqualsCondition_SingleValueMatch() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("one");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, conditionResponse));
    assertTrue(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }

  public void test_EqualsCondition_MultipleValueMatch() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");
    conditionResponse.addValue("two");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("one");
    actualResponse.addValue("two");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, conditionResponse));
    assertTrue(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }

  public void test_EqualsCondition_Mismatch() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("one");
    actualResponse.addValue("two");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, conditionResponse));
    assertFalse(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }

  public void test_EqualsCondition_VariableIgnoredWhenNotSpecifiedInCondition() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");

    AssessmentResponse actualResponse1 = new AssessmentResponse();
    actualResponse1.setResponseId("var5");
    actualResponse1.addValue("one");

    AssessmentResponse actualResponse2 = new AssessmentResponse();
    actualResponse2.setResponseId("var6");
    actualResponse2.addValue("lol!");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, conditionResponse));
    // This should ignore the value of var6 since it is not specified in the ResponseCondition.
    assertTrue(criteria.isSatisfied(Arrays.asList(actualResponse1, actualResponse2)));
  }

  public void test_ContainsCondition_SingleValueContainedInResponse() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("one");
    actualResponse.addValue("two");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, conditionResponse));
    assertTrue(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }

  public void test_ContainsCondition_AllValuesContainedInResponse() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");
    conditionResponse.addValue("two");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("one");
    actualResponse.addValue("two");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, conditionResponse));
    assertTrue(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }

  public void test_ContainsCondition_NoMatchingValues() {
    AssessmentResponse conditionResponse = new AssessmentResponse();
    conditionResponse.setResponseId("var5");
    conditionResponse.addValue("one");

    AssessmentResponse actualResponse = new AssessmentResponse();
    actualResponse.setResponseId("var5");
    actualResponse.addValue("three");

    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, conditionResponse));
    assertFalse(criteria.isSatisfied(Arrays.asList(actualResponse)));
  }
}
