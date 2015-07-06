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

import butterknife.Bind;
import butterknife.ButterKnife;
import lombok.Getter;
import lombok.Setter;

public class SurveyScreen extends LinearLayout {

  @Bind(R.id.content) LinearLayout contentLayout;

  private List<ISurveyComponent> surveyComponents = new ArrayList<>();
  private LinkedHashMap<ResponseCriteria, Action> actionMap = new LinkedHashMap<>();

  @Getter @Setter private String screenId;
  @Getter @Setter private String mainText;
  @Getter @Setter private NavigationButtonModel previousButtonModel;
  @Getter @Setter private NavigationButtonModel nextButtonModel;
  private ResponseCollectorService responseCollectorService = new ResponseCollectorService();

  public SurveyScreen(Context context, AttributeSet attrs) {
    super(context, attrs);
    ButterKnife.bind(this);
  }

  public void addSurveyComponent(ISurveyComponent surveyComponent) {
    contentLayout.addView(surveyComponent.getView());
    surveyComponents.add(surveyComponent);
  }

  public List<AssessmentResponse> collectResponses() {
    return responseCollectorService.collectResponses(surveyComponents);
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
}
