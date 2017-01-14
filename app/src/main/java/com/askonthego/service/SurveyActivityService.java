package com.askonthego.service;

import android.content.Context;

import com.askonthego.AssessmentHolder;
import com.askonthego.SurveyScreen;
import com.askonthego.actions.Action;
import com.askonthego.alarm.AssessmentTimeoutTask;
import com.askonthego.alarm.SurveySchedulerManager;
import com.askonthego.domain.Assessment;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.domain.AssessmentSaveOptions;
import com.askonthego.parser.StudyModel;
import com.askonthego.parser.SurveyModel;

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

    private AssessmentParser assessmentParser;
    private StudyParser studyParser;
    private AssessmentHolder assessmentHolder;

    private ParticipantDAO participantDAO;

    public SurveyActivityService(AssessmentParser assessmentParser, StudyParser studyParser, AssessmentHolder assessmentHolder, ParticipantDAO participantDAO) {
        this.assessmentParser = assessmentParser;
        this.studyParser = studyParser;
        this.assessmentHolder = assessmentHolder;
        this.participantDAO = participantDAO;
    }

    public void initStudyModel(InputStream surveyInputStream) {
        if (assessmentHolder.getStudyModel() == null) {
            StudyModel studyModel = studyParser.getStudy(surveyInputStream);
            assessmentHolder.setStudyModel(studyModel);
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
        currentAssessment.setEndDate(date);
        if (assessmentSaveOptions.isTimeout()) {
            currentAssessment.setTimeoutDate(date);
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
        assessment.setParticipant(participantDAO.getActiveParticipant());
        this.currentAssessment = assessment;

        SurveyModel surveyModel = getSurveyModel(surveyName, studyModel);
        List<SurveyScreen> surveyScreensList = assessmentParser.getScreens(surveyModel);
        this.surveyScreens.clear();
        for (SurveyScreen surveyScreen : surveyScreensList) {
            this.surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
        }

        String startScreenId = getStartScreenId();
        screenStack.clear();
        responseStack.clear();
        setCurrentScreen(startScreenId);
        screenStack.push(this.surveyScreens.get(startScreenId));

        currentAssessment.setStartDate(new Date());

        if (surveyModel.getTimeoutMinutes() > 0) {
            scheduleAssessmentTimeout(context, surveyModel.getTimeoutMinutes());
        }
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
