package com.example.jaf50.survey.service;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.example.jaf50.survey.R;
import com.example.jaf50.survey.SurveyScreen;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parser.CheckboxGroupModel;
import com.example.jaf50.survey.parser.ComponentModel;
import com.example.jaf50.survey.parser.DatePickerModel;
import com.example.jaf50.survey.parser.InputModel;
import com.example.jaf50.survey.parser.NavigationButtonModel;
import com.example.jaf50.survey.parser.OpenEndedModel;
import com.example.jaf50.survey.parser.RadioGroupModel;
import com.example.jaf50.survey.parser.ResponseConditionOperator;
import com.example.jaf50.survey.parser.ResponseCriteriaModel;
import com.example.jaf50.survey.parser.SliderModel;
import com.example.jaf50.survey.parser.SpacerModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.parser.SurveyScreenModel;
import com.example.jaf50.survey.parser.TextModel;
import com.example.jaf50.survey.parser.TimePickerModel;
import com.example.jaf50.survey.response.ResponseCondition;
import com.example.jaf50.survey.response.ResponseCriteria;
import com.example.jaf50.survey.ui.CheckboxComponent;
import com.example.jaf50.survey.ui.CheckboxGroupComponent;
import com.example.jaf50.survey.ui.DatePickerComponent;
import com.example.jaf50.survey.ui.ISurveyComponent;
import com.example.jaf50.survey.ui.OpenEndedComponent;
import com.example.jaf50.survey.ui.RadioButtonComponent;
import com.example.jaf50.survey.ui.RadioGroupComponent;
import com.example.jaf50.survey.ui.SliderComponent;
import com.example.jaf50.survey.ui.SpacerComponent;
import com.example.jaf50.survey.ui.TextComponent;
import com.example.jaf50.survey.ui.TimePickerComponent;

import java.util.ArrayList;
import java.util.List;

public class AssessmentUiBuilder {

  private Context context;
  private LayoutInflater layoutInflater;
  private Assessment assessment;

  public AssessmentUiBuilder(Context context, Assessment assessment) {
    this.context = context;
    this.layoutInflater = LayoutInflater.from(context);
    this.assessment = assessment;
  }

  public List<SurveyScreen> build(SurveyModel surveyModel) {
    List<SurveyScreen> surveyScreens = new ArrayList<>();
    for (SurveyScreenModel surveyScreenModel : surveyModel.getScreens()) {
      SurveyScreen surveyScreen = (SurveyScreen) layoutInflater.inflate(R.layout.survey_content, null);
      surveyScreen.setScreenId(surveyScreenModel.getId());
      surveyScreen.setPreviousButtonModel(getButtonModel(surveyScreenModel.getPrevious(), true, "Previous"));
      surveyScreen.setNextButtonModel(getButtonModel(surveyScreenModel.getNext(), true, "Next"));

      for (ComponentModel componentModel : surveyScreenModel.getComponents()) {
        surveyScreen.addSurveyComponent(buildComponent(componentModel));
      }

      for (ResponseCriteriaModel responseCriteriaModel : surveyScreenModel.getResponseCriteria()) {
        if (responseCriteriaModel.getCondition() == ResponseConditionOperator.EQUALS ||
            responseCriteriaModel.getCondition() == ResponseConditionOperator.CONTAINS) {
          AssessmentResponse assessmentResponse = new AssessmentResponse();
          assessmentResponse.setResponseId(responseCriteriaModel.getResponse().getId());
          assessmentResponse.setValues(responseCriteriaModel.getResponse().getValues());

          ResponseCondition responseCondition = new ResponseCondition(responseCriteriaModel.getCondition(), assessmentResponse);
          DirectContentTransition transition = new DirectContentTransition(responseCriteriaModel.getResponse().getId(),
              responseCriteriaModel.getTransition(),
              false);

          ResponseCriteria responseCriteria = new ResponseCriteria();
          responseCriteria.addCondition(responseCondition);

          surveyScreen.addResponseCriteria(responseCriteria, transition);
        } else if (responseCriteriaModel.getCondition() == ResponseConditionOperator.DEFAULT) {
          ResponseCriteria defaultResponseCriteria = new ResponseCriteria();
          defaultResponseCriteria.setDefault(true);
          defaultResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.DEFAULT, new AssessmentResponse()));

          surveyScreen.addResponseCriteria(defaultResponseCriteria, new DirectContentTransition(null,
              responseCriteriaModel.getTransition(),
              responseCriteriaModel.allowsSkipping()));
        } else if (responseCriteriaModel.getCondition() == ResponseConditionOperator.COMPLETE) {
          ResponseCriteria surveyCompleteResponseCriteria = new ResponseCriteria();
          surveyCompleteResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.COMPLETE, new AssessmentResponse()));

          surveyScreen.addResponseCriteria(surveyCompleteResponseCriteria, new EndAssessmentAction(assessment));
        }
      }

      surveyScreens.add(surveyScreen);
    }
    return surveyScreens;
  }

  private NavigationButtonModel getButtonModel(NavigationButtonModel parsedModel, boolean defaultAllowed, String defaultLabel) {
    NavigationButtonModel navigationButtonModel = new NavigationButtonModel();
    navigationButtonModel.setAllowed(defaultAllowed);
    navigationButtonModel.setLabel(defaultLabel);

    if (parsedModel != null) {
      if (parsedModel.isAllowed() != null) {
        navigationButtonModel.setAllowed(parsedModel.isAllowed());
      }
      if (parsedModel.getLabel() != null) {
        navigationButtonModel.setLabel(parsedModel.getLabel());
      }
    }
    return navigationButtonModel;
  }

  private ISurveyComponent buildComponent(ComponentModel componentModel) {
    if (componentModel instanceof TextModel) {
      return buildTextComponent((TextModel) componentModel);
    } else if (componentModel instanceof SliderModel) {
      return buildSeekBarComponent((SliderModel) componentModel);
    } else if (componentModel instanceof CheckboxGroupModel) {
      return buildCheckboxGroupComponent((CheckboxGroupModel) componentModel);
    } else if (componentModel instanceof RadioGroupModel) {
      return buildRadioGroupComponent((RadioGroupModel) componentModel);
    } else if (componentModel instanceof DatePickerModel) {
      return buildDatePickerComponent((DatePickerModel) componentModel);
    } else if (componentModel instanceof TimePickerModel) {
      return buildTimePickerComponent((TimePickerModel) componentModel);
    } else if (componentModel instanceof SpacerModel) {
      return buildSpacerComponent((SpacerModel) componentModel);
    } else if (componentModel instanceof OpenEndedModel) {
      return buildOpenEndedComponent((OpenEndedModel) componentModel);
    }
    throw new IllegalArgumentException("componentModel type " + componentModel.getClass() + " is not valid.");
  }

  private TextComponent buildTextComponent(TextModel model) {
    TextComponent questionTextComponent = (TextComponent) layoutInflater.inflate(R.layout.text_view, null);
    questionTextComponent.setText(model.getLabel());
    return questionTextComponent;
  }

  private SliderComponent buildSeekBarComponent(SliderModel model) {
    SliderComponent sliderComponent = (SliderComponent) layoutInflater.inflate(R.layout.slider, null);
    sliderComponent.setResponseId(model.getResponseId());
    sliderComponent.setLeftLabelText(model.getLeftLabel());
    sliderComponent.setRightLabelText(model.getRightLabel());
    return sliderComponent;
  }

  private CheckboxGroupComponent buildCheckboxGroupComponent(CheckboxGroupModel model) {
    CheckboxGroupComponent checkboxGroupComponent = (CheckboxGroupComponent) layoutInflater.inflate(R.layout.checkbox_group, null);
    checkboxGroupComponent.setResponseId(model.getResponseId());
    for (InputModel inputModel : model.getInputs()) {
      CheckboxComponent checkboxComponent = (CheckboxComponent) layoutInflater.inflate(R.layout.checkbox, null);
      checkboxComponent.setText(inputModel.getLabel());
      checkboxComponent.setValue(inputModel.getValue());
      checkboxGroupComponent.addComponent(checkboxComponent);
    }
    return checkboxGroupComponent;
  }

  private RadioGroupComponent buildRadioGroupComponent(RadioGroupModel model) {
    RadioGroupComponent radioGroupComponent = (RadioGroupComponent) layoutInflater.inflate(R.layout.radio_group, null);
    radioGroupComponent.setResponseId(model.getResponseId());
    for (InputModel inputModel : model.getInputs()) {
      RadioButtonComponent radioButtonComponent = (RadioButtonComponent) layoutInflater.inflate(R.layout.radio_button, null);
      radioButtonComponent.setText(inputModel.getLabel());
      radioButtonComponent.setValue(inputModel.getValue());
      radioGroupComponent.addComponent(radioButtonComponent);
    }
    return radioGroupComponent;
  }

  private DatePickerComponent buildDatePickerComponent(DatePickerModel model) {
    DatePickerComponent datePickerComponent = (DatePickerComponent) layoutInflater.inflate(R.layout.date_picker, null);
    datePickerComponent.setResponseId(model.getResponseId());
    datePickerComponent.setPickerStyle(model.getPickerStyle());
    datePickerComponent.setLabel(model.getLabel());
    return datePickerComponent;
  }

  private TimePickerComponent buildTimePickerComponent(TimePickerModel model) {
    TimePickerComponent timePickerComponent = (TimePickerComponent) layoutInflater.inflate(R.layout.time_picker, null);
    timePickerComponent.setResponseId(model.getResponseId());
    timePickerComponent.setPickerStyle(model.getPickerStyle());
    timePickerComponent.setLabel(model.getLabel());
    return timePickerComponent;
  }

  private ISurveyComponent buildSpacerComponent(SpacerModel model) {
    SpacerComponent spacerComponent = (SpacerComponent) layoutInflater.inflate(R.layout.spacer, null);
    int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, model.getHeight(), this.context.getResources().getDisplayMetrics());
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
    spacerComponent.setLayoutParams(params);
    return spacerComponent;
  }

  private ISurveyComponent buildOpenEndedComponent(OpenEndedModel model) {
    OpenEndedComponent openEndedComponent = (OpenEndedComponent) layoutInflater.inflate(R.layout.open_ended, null);
    openEndedComponent.setResponseId(model.getResponseId());
    return openEndedComponent;
  }
}
