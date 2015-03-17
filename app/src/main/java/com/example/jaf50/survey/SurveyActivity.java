package com.example.jaf50.survey;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.Participant;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AssessmentUiBuilder;
import com.orm.query.Condition;
import com.orm.query.Select;

import java.util.List;

public class SurveyActivity extends FragmentActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    AssessmentParserService assessmentParserService = new AssessmentParserService();
    SurveyModel surveyModel = assessmentParserService.parse(getResources().openRawResource(R.raw.survey));

    Survey survey = new Survey().setName("My Survey");
    survey.save();

    Assessment assessment = new Assessment()
        .setDescription(surveyModel.getDescription())
        .setSurvey(survey);

    // TODO - get the "real" Participant for this AssessmentSession.
    Participant participant;
    List<Participant> savedParticipants = Select.from(Participant.class).where(Condition.prop("assigned_id = ?").eq("123")).list();
    if (savedParticipants.isEmpty()) {
      participant = new Participant().setAssignedId("123");
      participant.save();
    } else {
      participant = savedParticipants.get(0);
    }

    AssessmentSession.getInstance().setParticipant(participant);
    AssessmentSession.getInstance().setAssessment(assessment);

    AssessmentUiBuilder assessmentUiBuilder = new AssessmentUiBuilder(this, assessment);
    List<SurveyScreen> surveyScreens = assessmentUiBuilder.build(surveyModel);

    SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    for (SurveyScreen screen : surveyScreens) {
      fragment.addSurveyScreen(screen);
    }

    fragment.startSurvey(surveyScreens.get(0).getScreenId());
  }
}
