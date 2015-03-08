package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.parser.NavigationButtonModel;
import com.example.jaf50.survey.response.Response;
import com.example.jaf50.survey.response.ResponseCriteria;
import com.example.jaf50.survey.ui.ISurveyComponent;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SurveyScreen extends LinearLayout {

  @InjectView(R.id.content)
  LinearLayout contentLayout;

  private List<ISurveyComponent> surveyComponents = new ArrayList<>();
  private LinkedHashMap<ResponseCriteria, Action> actionMap = new LinkedHashMap<>();

  private String screenId;
  private Assessment associatedAssessment;

  private NavigationButtonModel previousButtonModel;
  private NavigationButtonModel nextButtonModel;

  public SurveyScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    ButterKnife.inject(this);
  }

  public void addSurveyComponent(ISurveyComponent surveyComponent) {
    contentLayout.addView(surveyComponent.getView());
    surveyComponents.add(surveyComponent);
  }

  public void setScreenId(String screenId) {
    this.screenId = screenId;
  }

  public String getScreenId() {
    return screenId;
  }

  public Assessment getAssociatedAssessment() {
    return associatedAssessment;
  }

  public void setAssociatedAssessment(Assessment associatedAssessment) {
    this.associatedAssessment = associatedAssessment;
  }

  public List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> responses = new ArrayList<>();
    Date responseDate = new Date();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        AssessmentResponse assessmentResponse = new AssessmentResponse();
        Response response = surveyComponent.getResponse();
        assessmentResponse.setResponseDate(responseDate);
        assessmentResponse.setAssessment(associatedAssessment);
        assessmentResponse.setResponseId(response.getId());

        List <Value> values = new ArrayList<>();
        for (Object rawValue : response.getValues()) {
          if (rawValue != null) {
            Value value = new Value();
            value.setResponse(assessmentResponse);
            value.setValue(rawValue.toString());
            values.add(value);
          }
        }
        assessmentResponse.setValues(values);

        responses.add(assessmentResponse);
      }
    }

    return responses;
  }

  public Action getAction() {
    List<AssessmentResponse> responses = collectResponses();
    for (ResponseCriteria responseCriteria: actionMap.keySet()) {
      if (responseCriteria.isSatisfied(responses)) {
        return actionMap.get(responseCriteria);
      }
    }
    return null;
  }

  public void addResponseCriteria(ResponseCriteria responseCriteria, Action correspondingAction) {
    actionMap.put(responseCriteria, correspondingAction);
  }

  public NavigationButtonModel getPreviousButtonModel() {
    return previousButtonModel;
  }

  public void setPreviousButtonModel(NavigationButtonModel previousButtonModel) {
    this.previousButtonModel = previousButtonModel;
  }

  public NavigationButtonModel getNextButtonModel() {
    return nextButtonModel;
  }

  public void setNextButtonModel(NavigationButtonModel nextButtonModel) {
    this.nextButtonModel = nextButtonModel;
  }
}
