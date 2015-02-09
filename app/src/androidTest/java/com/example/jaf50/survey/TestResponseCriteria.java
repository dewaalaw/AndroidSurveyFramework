package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import java.util.Arrays;

public class TestResponseCriteria extends AndroidTestCase {

  public void test_EqualsCondition_SingleValueMatch() {
    ResponseCriteria criteria = new ResponseCriteria();
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));

    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one"))));
  }

  public void test_EqualsCondition_MultipleValueMatch() {
    ResponseCriteria criteria = new ResponseCriteria();
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one").addValue("two")));

    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one").addValue("two"))));
  }

  public void test_EqualsCondition_Mismatch() {
    ResponseCriteria criteria = new ResponseCriteria();
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));

    assertFalse(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one").addValue("two"))));
  }

  public void test_EqualsCondition_VariableIgnoredWhenNotSpecifiedInCondition() {
    ResponseCriteria criteria = new ResponseCriteria();
    criteria.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));
    // This should ignore the value of var6 since it is not specified in the ResponseCondition.
    assertTrue(criteria.isSatisfied(Arrays.asList(new Response("var5").addValue("one"), new Response("var6").addValue("lol!"))));
  }
}
