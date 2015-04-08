package com.example.jaf50.survey;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class SurveyActivityService {

  private HashMap<String, SurveyScreen> surveyScreens = new HashMap<>();
  private SurveyScreen currentScreen;
  private Stack<SurveyScreen> screenStack = new Stack<>();
  private Stack<List<AssessmentResponse>> responseStack = new Stack<>();
  private Assessment currentAssessment;

  public boolean hasPrevious() {
    return screenStack.size() >= 2;
  }

  public SurveyScreen previous() {
    // Remove the current screen from the top of the stack.
    screenStack.pop();
    responseStack.pop();
    // Then peek to get the previous screen.
    SurveyScreen previousSurveyScreen = screenStack.peek();
    ASYNC_setCurrentScreen(previousSurveyScreen.getScreenId());

    return previousSurveyScreen;
  }

  public void ASYNC_setCurrentScreen(String screenId) {
    SurveyScreen surveyScreen = surveyScreens.get(screenId);
    if (surveyScreen == null) {
      throw new IllegalArgumentException("Invalid survey screen id specified: '" + screenId + "'.");
    }
    currentScreen = surveyScreen;
  }

  public Action getCurrentScreenAction() {
    return currentScreen.getAction();
  }

  public void ASYNC_saveAssessmentNow() throws ParseException {
    currentAssessment.setResponses(ASYNC_collectResponses());
    currentAssessment.pinInBackground();
    currentAssessment.save();
  }

  public void ASYNC_saveAssessmentEventually() throws ParseException {
    currentAssessment.setResponses(ASYNC_collectResponses());
    currentAssessment.pinInBackground();
    currentAssessment.saveEventually();
  }

  private List<AssessmentResponse> ASYNC_collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();
    for (List<AssessmentResponse> responses : responseStack) {
      assessmentResponses.addAll(responses);
    }
    return assessmentResponses;
  }

  public void ASYNC_transitionToNextScreen(String toScreenId) {
    responseStack.push(currentScreen.collectResponses());
    ASYNC_setCurrentScreen(toScreenId);
    SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
    screenStack.push(surveyScreen);
    //UI_setCurrentScreen(toScreenId);
  }

  public void ASYNC_setSurveyScreens(List<SurveyScreen> surveyScreens) {
    this.surveyScreens.clear();
    for (SurveyScreen surveyScreen : surveyScreens) {
      this.surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
    }
  }

  public void ASYNC_startSurvey(String startScreenId) {
    screenStack.clear();
    responseStack.clear();
    // TODO - any other setup upon survey start (e.g. capture start timestamp).
    ASYNC_setCurrentScreen(startScreenId);
    SurveyScreen startSurveyScreen = surveyScreens.get(startScreenId);
    screenStack.push(startSurveyScreen);

//    UI_setCurrentScreen(startScreenId);
//    UI_setAssessmentState(AssessmentState.Starting);
  }

  public void ASYNC_setCurrentAssessment(Assessment currentAssessment) {
    this.currentAssessment = currentAssessment;
  }

  public SurveyModel ASYNC_getSurveyModel(String surveyName, StudyModel studyModel) {
    for (SurveyModel surveyModel : studyModel.getSurveys()) {
      if (surveyModel.getName().equals(surveyName)) {
        return surveyModel;
      }
    }
    return null;
  }

  public SurveyScreen getCurrentScreen() {
    return currentScreen;
  }

  public SurveyScreen getScreen(String screenId) {
    return surveyScreens.get(screenId);
  }

  public Assessment getCurrentAssessment() {
    return currentAssessment;
  }
}
