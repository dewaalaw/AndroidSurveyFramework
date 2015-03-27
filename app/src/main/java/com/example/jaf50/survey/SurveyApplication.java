package com.example.jaf50.survey;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Survey;
import com.orm.SugarApp;
import com.parse.Parse;
import com.parse.ParseObject;

public class SurveyApplication extends SugarApp {

  private static final String APPLICATION_ID = "M8rcJZvA3poUvvJofyS4t5K0vtpHVLg3biM1NgVK";
  private static final String CLIENT_KEY = "JPImlAeJvXtmuaTH1mmzcqe87zuOtXaUvgJTzERX";

  public static void registerParseClasses() {
    ParseObject.registerSubclass(Survey.class);
    ParseObject.registerSubclass(Assessment.class);
    ParseObject.registerSubclass(AssessmentResponse.class);
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Parse.enableLocalDatastore(this);
    Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);

    registerParseClasses();

//    for (int i = 0; i < 100; i++){
//      Survey survey = new Survey();
//      survey.setName("My Survey");
//
//      Participant participant = new Participant();
//      participant.setAssignedId("123");
//
//      AssessmentResponse assessmentResponse1 = new AssessmentResponse();
//      assessmentResponse1.setResponseId("var1");
//      assessmentResponse1.setResponseDate(new Date());
//      assessmentResponse1.addValue("val" + i);
//
//      AssessmentResponse assessmentResponse2 = new AssessmentResponse();
//      assessmentResponse2.setResponseId("var2");
//      assessmentResponse2.setResponseDate(new Date());
//      assessmentResponse2.addValue("val" + i);
//
//      Assessment assessment = new Assessment();
//      assessment.setParticipant(participant);
//      assessment.setSurvey(survey);
//      assessment.setResponses(assessmentResponse1, assessmentResponse2);
//      assessment.saveInBackground();
//
//      try {
//        Thread.sleep(50);
//      } catch (Exception e) {
//      }
//    }
  }
}
