package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.parser.ResponseConditionOperator;
import com.example.jaf50.survey.response.ResponseCondition;
import com.example.jaf50.survey.response.ResponseCriteria;

import java.util.Arrays;

public class TestResponseCriteria extends AndroidTestCase {

  ResponseCriteria criteria;

  @Override
  protected void setUp() throws Exception {
    criteria = new ResponseCriteria();
  }

  public void test_EqualsCondition_SingleValueMatch() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, new SurveyResponse("var5").addValue("one")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("one"))));
  }

  public void test_EqualsCondition_MultipleValueMatch() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, new SurveyResponse("var5").addValue(new Value().setValue("one")).addValue(new Value().setValue("two"))));
    assertTrue(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue(new Value().setValue("one")).addValue(new Value().setValue("two")))));
  }

  public void test_EqualsCondition_Mismatch() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, new SurveyResponse("var5").addValue("one")));
    assertFalse(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("one").addValue("two"))));
  }

  public void test_EqualsCondition_VariableIgnoredWhenNotSpecifiedInCondition() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.EQUALS, new SurveyResponse("var5").addValue("one")));
    // This should ignore the value of var6 since it is not specified in the ResponseCondition.
    assertTrue(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("one"), new SurveyResponse("var6").addValue("lol!"))));
  }

  public void test_ContainsCondition_SingleValueContainedInResponse() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, new SurveyResponse("var5").addValue("one")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("one").addValue("two"))));
  }

  public void test_ContainsCondition_AllValuesContainedInResponse() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, new SurveyResponse("var5").addValue("one").addValue("two")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("two").addValue("one"))));
  }

  public void test_ContainsCondition_NoMatchingValues() {
    criteria.addCondition(new ResponseCondition(ResponseConditionOperator.CONTAINS, new SurveyResponse("var5").addValue("one")));
    assertFalse(criteria.isSatisfied(Arrays.asList(new SurveyResponse("var5").addValue("three"))));
  }
}
