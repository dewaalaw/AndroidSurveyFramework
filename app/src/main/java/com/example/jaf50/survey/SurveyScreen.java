package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.NavigationButtonModel;
import com.example.jaf50.survey.response.ResponseCriteria;
import com.example.jaf50.survey.service.ResponseCollectorService;
import com.example.jaf50.survey.ui.ISurveyComponent;

import java.util.ArrayList;
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
  private String mainText;
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

  public void setMainText(String mainText) {
    this.mainText = mainText;
  }

  public String getMainText() {
    return mainText;
  }

  public List<AssessmentResponse> collectResponses() {
    return new ResponseCollectorService().collectResponses(surveyComponents);
  }

  /**
   * Returns true if all responses have been entered on this screen; false otherwise.
   */
  public boolean responsesEntered() {
    List<AssessmentResponse> screenResponses = collectResponses();
    for (AssessmentResponse screenResponse : screenResponses) {
      if (screenResponse.isEmpty()) {
        return false;
      }
    }
    return true;
  }

  public Action getAction() {
    if (responsesEntered()) {
      List<AssessmentResponse> responses = collectResponses();
      for (ResponseCriteria responseCriteria : actionMap.keySet()) {
        if (responseCriteria.isSatisfied(responses)) {
          return actionMap.get(responseCriteria);
        }
      }
    } else {
      // Return the default response criteria, if one exists.
      for (ResponseCriteria responseCriteria : actionMap.keySet()) {
        if (responseCriteria.isDefault()) {
          return actionMap.get(responseCriteria);
        }
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
