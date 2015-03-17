package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.domain.Value;
import com.orm.SugarApp;

import java.util.Date;

public class TestAssessment extends AndroidTestCase {

  @Override
  protected void setUp() throws Exception {
    SugarApp.getSugarContext().onCreate();
    SurveyTestUtils.deleteAllRecords();
  }

  @Override
  protected void tearDown() throws Exception {
    SurveyTestUtils.deleteAllRecords();
  }

  public void test_GetAssessments_WhenNoAssessmentsSavedForSurvey() {
    Survey survey = new Survey().setName("My Survey");
    survey.save();

    Participant participant = new Participant().setAssignedId("123");
    participant.eagerLoad(survey);

    assertEquals(0, participant.getAssessments().size());
  }

  public void test_Save() {
    Date response1Date = new Date();
    Date response2Date = new Date();

    Survey survey = new Survey().setName("My Survey");
    survey.save();

    Participant participant = new Participant().setAssignedId("123");
    AssessmentResponse assessmentResponse1 = new AssessmentResponse().setResponseId("var1").setResponseDate(response1Date).addValue(new Value().setValue("val1"));
    AssessmentResponse assessmentResponse2 = new AssessmentResponse().setResponseId("var2").setResponseDate(response1Date).addValue(new Value().setValue("val2"));
    Assessment assessment = new Assessment().setParticipant(participant).setSurvey(survey).setDescription("Description").setResponses(assessmentResponse1, assessmentResponse2);
    assessment.save();

    participant.eagerLoad(survey);

    assertEquals(1, participant.getAssessments().size());
    assertEquals("Description", participant.getAssessments().get(0).getDescription());
    assertEquals(2, participant.getAssessments().get(0).getResponses().size());
    assertEquals("var1", participant.getAssessments().get(0).getResponses().get(0).getResponseId());
    assertEquals("var2", participant.getAssessments().get(0).getResponses().get(1).getResponseId());
    assertEquals(response1Date, participant.getAssessments().get(0).getResponses().get(0).getResponseDate());
    assertEquals(response2Date, participant.getAssessments().get(0).getResponses().get(1).getResponseDate());
    assertEquals(1, participant.getAssessments().get(0).getResponses().get(0).getValues().size());
    assertEquals(1, participant.getAssessments().get(0).getResponses().get(1).getValues().size());
    assertEquals("val1", participant.getAssessments().get(0).getResponses().get(0).getValues().get(0).getValue());
    assertEquals("val2", participant.getAssessments().get(0).getResponses().get(1).getValues().get(0).getValue());
  }
}
