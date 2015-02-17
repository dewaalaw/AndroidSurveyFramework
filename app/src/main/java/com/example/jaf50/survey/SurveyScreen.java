package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.response.Response;
import com.example.jaf50.survey.response.ResponseCriteria;
import com.example.jaf50.survey.ui.ISurveyComponent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SurveyScreen extends LinearLayout {

  @InjectView(R.id.content)
  LinearLayout contentLayout;

  private List<ISurveyComponent> surveyComponents = new ArrayList<ISurveyComponent>();
  private LinkedHashMap<ResponseCriteria, Action> actionMap = new LinkedHashMap<>();

  private String screenId;

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

  public List<Response> getResponses() {
    List<Response> responses = new ArrayList<Response>();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        responses.add(surveyComponent.getResponse());
      }
    }
    return responses;
  }

  public Action getAction() {
    List<Response> responses = getResponses();
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
}
