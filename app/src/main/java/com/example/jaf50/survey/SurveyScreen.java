package com.example.jaf50.survey;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.domain.Value;
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

  private List<ISurveyComponent> surveyComponents = new ArrayList<>();
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

  public List<SurveyResponse> getResponses() {
    List<SurveyResponse> responses = new ArrayList<>();
    for (ISurveyComponent surveyComponent : surveyComponents) {
      if (surveyComponent.acceptsResponse()) {
        SurveyResponse surveyResponse = new SurveyResponse();
        Response response = surveyComponent.getResponse();
//        surveyResponse.setResponseDate(responseDate);
//        surveyResponse.setSurvey(survey);
        surveyResponse.setResponseId(response.getId());

        List <Value> values = new ArrayList<>();
        for (Object rawValue : response.getValues()) {
          if (rawValue != null) {
            Value value = new Value();
            value.setSurveyResponse(surveyResponse);
            value.setValue(rawValue.toString());
            values.add(value);
          }
        }
        surveyResponse.setValues(values);

        responses.add(surveyResponse);
      }
    }

    return responses;
  }

  public Action getAction() {
    List<SurveyResponse> responses = getResponses();
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

//  public void generateResponses(Date responseDate, Survey survey) {
//    List<SurveyResponse> responses = new ArrayList<>();
//    for (ISurveyComponent surveyComponent : surveyComponents) {
//      if (surveyComponent.acceptsResponse()) {
//        SurveyResponse surveyResponse = new SurveyResponse();
//        Response response = surveyComponent.getResponse();
//        surveyResponse.setResponseDate(responseDate);
//        surveyResponse.setSurvey(survey);
//        surveyResponse.setResponseId(response.getId());
//
//        List <Value> values = new ArrayList<>();
//        for (Object rawValue : response.getValues()) {
//          if (rawValue != null) {
//            Value value = new Value();
//            value.setSurveyResponse(surveyResponse);
//            value.setValue(value.toString());
//            values.add(value);
//          }
//        }
//        surveyResponse.setValues(values);
//
//        responses.add(surveyResponse);
//      }
//    }
//
//    this.surveyResponses = responses;
//  }
}
