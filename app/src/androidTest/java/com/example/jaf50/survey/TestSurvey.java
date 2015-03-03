package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.domain.Value;
import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestSurvey extends AndroidTestCase {

  @Override
  protected void setUp() throws Exception {
    SugarApp.getSugarContext().onCreate();

    if (SurveyResponse.listAll(SurveyResponse.class).size() > 0) {
      SurveyResponse.deleteAll(SurveyResponse.class);
    }
    if (Survey.listAll(Survey.class).size() > 0) {
      Survey.deleteAll(Survey.class);
    }
  }

  @Override
  protected void tearDown() throws Exception {
    if (SurveyResponse.listAll(SurveyResponse.class).size() > 0) {
      SurveyResponse.deleteAll(SurveyResponse.class);
    }
    if (Survey.listAll(Survey.class).size() > 0) {
      Survey.deleteAll(Survey.class);
    }
  }

  public void testSurveySaveAndRetrieve() {
    Date response1Date = new Date();
    Date response2Date = new Date();

    SurveyResponse surveyResponse1 = new SurveyResponse();
    surveyResponse1.setResponseId("var1");
    surveyResponse1.setResponseDate(response1Date);
    surveyResponse1.addValue(new Value().setValue("val1"));

    SurveyResponse surveyResponse2 = new SurveyResponse();
    surveyResponse2.setResponseId("var2");
    surveyResponse2.setResponseDate(response2Date);
    surveyResponse2.addValue(new Value().setValue("val2"));

    Survey survey = new Survey();
    survey.setName("My Survey");
    survey.setDescription("Description");
    survey.setResponses(Arrays.asList(surveyResponse1, surveyResponse2));
    survey.save();

    List<Survey> surveys = Select.from(Survey.class).where(Condition.prop("name").eq("My Survey")).list();
    assertEquals(1, surveys.size());
    assertEquals("Description", surveys.get(0).getDescription());

    assertEquals(2, surveys.get(0).getResponses().size());
    assertEquals("var1", surveys.get(0).getResponses().get(0).getResponseId());
    assertEquals("var2", surveys.get(0).getResponses().get(1).getResponseId());
    assertEquals(response1Date, surveys.get(0).getResponses().get(0).getResponseDate());
    assertEquals(response2Date, surveys.get(0).getResponses().get(1).getResponseDate());
    assertEquals(1, surveys.get(0).getResponses().get(0).getValues().size());
    assertEquals(1, surveys.get(0).getResponses().get(1).getValues().size());
    assertEquals("val1", surveys.get(0).getResponses().get(0).getValues().get(0).getValue());
    assertEquals("val2", surveys.get(0).getResponses().get(1).getValues().get(0).getValue());
  }
}
