package com.example.jaf50.survey;

import android.content.Context;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.alarm.AssessmentTimeoutTask;
import com.example.jaf50.survey.alarm.SurveySchedulerManager;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.AssessmentSaveOptions;
import com.example.jaf50.survey.parser.StudyModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.service.AssessmentParserService;
import com.example.jaf50.survey.service.AssessmentUiBuilderService;
import com.parse.ParseUser;

import java.io.InputStream;
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

  public void initStudyModel(InputStream surveyInputStream) {
    if (AssessmentHolder.getInstance().getStudyModel() == null) {
      AssessmentParserService assessmentParserService = new AssessmentParserService();
      StudyModel studyModel = assessmentParserService.parseStudy(surveyInputStream);
      AssessmentHolder.getInstance().setStudyModel(studyModel);
    }
  }

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

  public Assessment collectAssessment(AssessmentSaveOptions assessmentSaveOptions) {
    currentAssessment.setResponses(collectResponses());
    Date date = new Date();
    currentAssessment.setAssessmentEndDate(date);
    if (assessmentSaveOptions.isTimeout()) {
      currentAssessment.setAssessmentTimeoutDate(date);
    }
    return currentAssessment;
  }

  private List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();

    for (List<AssessmentResponse> responses : responseStack) {
      for (AssessmentResponse response : responses) {
        assessmentResponses.add(response);
      }
    }

    return assessmentResponses;
  }

  public void transitionToScreen(String toScreenId) {
    responseStack.push(currentScreen.collectResponses());
    setCurrentScreen(toScreenId);
    SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
    screenStack.push(surveyScreen);
  }

  public void startSurvey(String surveyName, StudyModel studyModel, Context context) {
    Assessment assessment = new Assessment();
    assessment.setSynced(false);
    assessment.setSurveyName(surveyName);
    assessment.setParticipant(ParseUser.getCurrentUser());
    this.currentAssessment = assessment;

    SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
    List<SurveyScreen> surveyScreensList = buildSurveyScreens(surveyModel, context, assessment);
    this.surveyScreens.clear();
    for (SurveyScreen surveyScreen : surveyScreensList) {
      this.surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
    }

    String startScreenId = getStartScreenId();
    screenStack.clear();
    responseStack.clear();
    setCurrentScreen(startScreenId);
    screenStack.push(this.surveyScreens.get(startScreenId));

    currentAssessment.setAssessmentStartDate(new Date());

    if (surveyModel.getTimeoutMinutes() > 0) {
      scheduleAssessmentTimeout(context, surveyModel.getTimeoutMinutes());
    }
  }

  private List<SurveyScreen> buildSurveyScreens(SurveyModel surveyModel, Context context, Assessment assessment) {
    AssessmentUiBuilderService assessmentUiBuilderService = new AssessmentUiBuilderService(context, assessment);
    return assessmentUiBuilderService.build(surveyModel);
  }

  public String getStartScreenId() {
    return surveyScreens.keySet().iterator().next();
  }

  private void scheduleAssessmentTimeout(Context context, int timeoutMinutes) {
    SurveySchedulerManager.getInstance().stop(context, AssessmentTimeoutTask.class);
    // Need to save the timeout task with a valid cron expression, even though the task will be scheduled as a "one shot".
    SurveySchedulerManager.getInstance().saveTask(context, "0 0 1 1 *", AssessmentTimeoutTask.class);
    SurveySchedulerManager.getInstance().runNow(context, AssessmentTimeoutTask.class, timeoutMinutes * 60 * 1000);
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

  private SurveyModel getSurveyModel(String surveyName, StudyModel studyModel) {
    for (SurveyModel surveyModel : studyModel.getSurveys()) {
      if (surveyModel.getName().equals(surveyName)) {
        return surveyModel;
      }
    }
    return null;
  }
}
