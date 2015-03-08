package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Value;
import com.orm.SugarApp;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TestAssessment extends AndroidTestCase {

  @Override
  protected void setUp() throws Exception {
    SugarApp.getSugarContext().onCreate();

    if (AssessmentResponse.listAll(AssessmentResponse.class).size() > 0) {
      AssessmentResponse.deleteAll(AssessmentResponse.class);
    }
    if (Assessment.listAll(Assessment.class).size() > 0) {
      Assessment.deleteAll(Assessment.class);
    }
  }

  @Override
  protected void tearDown() throws Exception {
    if (AssessmentResponse.listAll(AssessmentResponse.class).size() > 0) {
      AssessmentResponse.deleteAll(AssessmentResponse.class);
    }
    if (Assessment.listAll(Assessment.class).size() > 0) {
      Assessment.deleteAll(Assessment.class);
    }
  }

  public void testSurveySaveAndRetrieve() {
    Date response1Date = new Date();
    Date response2Date = new Date();

    AssessmentResponse assessmentResponse1 = new AssessmentResponse();
    assessmentResponse1.setResponseId("var1");
    assessmentResponse1.setResponseDate(response1Date);
    assessmentResponse1.addValue(new Value().setValue("val1"));

    AssessmentResponse assessmentResponse2 = new AssessmentResponse();
    assessmentResponse2.setResponseId("var2");
    assessmentResponse2.setResponseDate(response2Date);
    assessmentResponse2.addValue(new Value().setValue("val2"));

    Assessment assessment = new Assessment();
    assessment.setName("My Survey");
    assessment.setDescription("Description");
    assessment.setResponses(Arrays.asList(assessmentResponse1, assessmentResponse2));
    assessment.save();

    List<Assessment> assessments = Select.from(Assessment.class).where(Condition.prop("name").eq("My Survey")).list();
    assertEquals(1, assessments.size());
    assertEquals("Description", assessments.get(0).getDescription());

    assertEquals(2, assessments.get(0).getResponses().size());
    assertEquals("var1", assessments.get(0).getResponses().get(0).getResponseId());
    assertEquals("var2", assessments.get(0).getResponses().get(1).getResponseId());
    assertEquals(response1Date, assessments.get(0).getResponses().get(0).getResponseDate());
    assertEquals(response2Date, assessments.get(0).getResponses().get(1).getResponseDate());
    assertEquals(1, assessments.get(0).getResponses().get(0).getValues().size());
    assertEquals(1, assessments.get(0).getResponses().get(1).getValues().size());
    assertEquals("val1", assessments.get(0).getResponses().get(0).getValues().get(0).getValue());
    assertEquals("val2", assessments.get(0).getResponses().get(1).getValues().get(0).getValue());
  }
}
