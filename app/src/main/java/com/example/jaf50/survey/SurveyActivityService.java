package com.example.jaf50.survey;

import android.content.Context;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.AssessmentSaveOptions;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentUiBuilderService;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Stack;

public class SurveyActivityService {

  private LinkedHashMap<String, SurveyScreen> surveyScreens = new LinkedHashMap<>();
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
    setCurrentScreen(previousSurveyScreen.getScreenId());

    return previousSurveyScreen;
  }

  public void setCurrentScreen(String screenId) {
    SurveyScreen surveyScreen = surveyScreens.get(screenId);
    if (surveyScreen == null) {
      throw new IllegalArgumentException("Invalid survey screen id specified: '" + screenId + "'.");
    }
    currentScreen = surveyScreen;
  }

  public Action getCurrentScreenAction() {
    return currentScreen.getAction();
  }

  public void saveAssessmentNow(AssessmentSaveOptions assessmentSaveOptions) throws ParseException {
    saveAssessmentLocally(assessmentSaveOptions);
    currentAssessment.save();
  }

  public void saveAssessmentEventually(AssessmentSaveOptions assessmentSaveOptions) throws ParseException {
    saveAssessmentLocally(assessmentSaveOptions);
    currentAssessment.saveEventually();
  }

  private void saveAssessmentLocally(AssessmentSaveOptions assessmentSaveOptions) {
    currentAssessment.setResponses(collectResponses());
    Date date = new Date();
    currentAssessment.setAssessmentEndDate(date);
    if (assessmentSaveOptions.isTimeout()) {
      currentAssessment.setAssessmentTimeoutDate(date);
    }
    currentAssessment.pinInBackground();
  }

  private List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();
    for (List<AssessmentResponse> responses : responseStack) {
      assessmentResponses.addAll(responses);
    }
    return assessmentResponses;
  }

  public void transitionToScreen(String toScreenId) {
    responseStack.push(currentScreen.collectResponses());
    setCurrentScreen(toScreenId);
    SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
    screenStack.push(surveyScreen);
  }

  public void startSurvey() {
    startSurvey(getStartScreenId());
  }

  public String getStartScreenId() {
    return surveyScreens.keySet().iterator().next();
  }

  public void startSurvey(String startScreenId) {
    screenStack.clear();
    responseStack.clear();
    setCurrentScreen(startScreenId);
    screenStack.push(surveyScreens.get(startScreenId));

    currentAssessment.setAssessmentStartDate(new Date());
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

  public void initAssessment(String surveyName, StudyModel studyModel, Context context) {
    Assessment assessment = new Assessment();
    assessment.setSurveyName(surveyName);
    assessment.setParticipant(ParseUser.getCurrentUser());
    this.currentAssessment = assessment;

    AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(context, assessment);
    SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
    List<SurveyScreen> surveyScreens = assessmentUiBuilderService.build(surveyModel);

    this.surveyScreens.clear();
    for (SurveyScreen surveyScreen : surveyScreens) {
      this.surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
    }
  }

  private SurveyModel getSurveyModel(String surveyName, StudyModel studyModel) {
    for (SurveyModel surveyModel : studyModel.getSurveys()) {
      if (surveyModel.getName().equals(surveyName)) {
        return surveyModel;
      }
    }
    return null;
  }
}
