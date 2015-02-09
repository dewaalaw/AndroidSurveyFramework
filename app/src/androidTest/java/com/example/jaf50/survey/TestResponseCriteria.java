package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import java.util.Arrays;

public class TestResponseCriteria extends AndroidTestCase {

  ResponseCriteria criteria;

  @Override
  protected void setUp() throws Exception {
    criteria = new ResponseCriteria();
  }

  public void test_EqualsCondition_SingleValueMatch() {
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one"))));
  }

  public void test_EqualsCondition_MultipleValueMatch() {
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one").addValue("two")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one").addValue("two"))));
  }

  public void test_EqualsCondition_Mismatch() {
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));
    assertFalse(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one").addValue("two"))));
  }

  public void test_EqualsCondition_VariableIgnoredWhenNotSpecifiedInCondition() {
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));
    // This should ignore the value of var6 since it is not specified in the ResponseCondition.
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one"), new Response("var6").addValue("lol!"))));
  }

  public void test_ContainsCondition_SingleValueContainedInResponse() {
    criteria.addCondition(new ResponseCondition("contains", new Response("var5").addValue("one")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one").addValue("two"))));
  }

  public void test_ContainsCondition_AllValuesContainedInResponse() {
    criteria.addCondition(new ResponseCondition("contains", new Response("var5").addValue("one").addValue("two")));
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("two").addValue("one"))));
  }

  public void test_ContainsCondition_NoMatchingValues() {
    criteria.addCondition(new ResponseCondition("contains", new Response("var5").addValue("one")));
    assertFalse(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("three"))));
  }
}
