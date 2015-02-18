package com.example.jaf50.survey.actions;

import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.domain.SurveyResponse;

import java.util.List;

public class EndSurveyAction implements Action {

  private Survey survey;
  private List<SurveyResponse> surveyResponses;

  public EndSurveyAction(Survey survey) {
    this.survey = survey;
  }

  public void setSurveyResponses(List<SurveyResponse> surveyResponses) {
    this.surveyResponses = surveyResponses;
  }

  @Override
  public void execute() {
    survey.setResponses(surveyResponses);
    survey.save();
  }
}
