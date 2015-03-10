package com.example.jaf50.survey.service;

import android.view.LayoutInflater;

import com.example.jaf50.survey.R;
import com.example.jaf50.survey.SurveyScreen;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.parser.CheckboxGroupModel;
import com.example.jaf50.survey.parser.ComponentModel;
import com.example.jaf50.survey.parser.DatePickerModel;
import com.example.jaf50.survey.parser.InputModel;
import com.example.jaf50.survey.parser.NavigationButtonModel;
import com.example.jaf50.survey.parser.RadioGroupModel;
import com.example.jaf50.survey.parser.ResponseConditionOperator;
import com.example.jaf50.survey.parser.ResponseCriteriaModel;
import com.example.jaf50.survey.parser.SliderModel;
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
import com.example.jaf50.survey.ui.RadioButtonComponent;
import com.example.jaf50.survey.ui.RadioGroupComponent;
import com.example.jaf50.survey.ui.SeekBarComponent;
import com.example.jaf50.survey.ui.TextComponent;
import com.example.jaf50.survey.ui.TimePickerComponent;

import java.util.ArrayList;
import java.util.List;

public class AssessmentUiBuilder {

  private LayoutInflater layoutInflater;

  public AssessmentUiBuilder(LayoutInflater layoutInflater) {
    this.layoutInflater = layoutInflater;
  }

  public List<SurveyScreen> build(SurveyModel surveyModel) {
    Assessment assessment = new Assessment();
    assessment.setDescription(surveyModel.getDescription());
    assessment.setName(surveyModel.getName());

    List<SurveyScreen> surveyScreens = new ArrayList<>();
    for (SurveyScreenModel surveyScreenModel : surveyModel.getScreens()) {
      SurveyScreen surveyScreen = (SurveyScreen) layoutInflater.inflate(R.layout.survey_content, null);
      surveyScreen.setScreenId(surveyScreenModel.getId());
      surveyScreen.setAssociatedAssessment(assessment);

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
          assessmentResponse.setAssessment(assessment);
          assessmentResponse.setValues(buildValues(responseCriteriaModel));

          ResponseCondition responseCondition = new ResponseCondition(responseCriteriaModel.getCondition(), assessmentResponse);
          DirectContentTransition transition = new DirectContentTransition(responseCriteriaModel.getResponse().getId(), responseCriteriaModel.getTransition());

          ResponseCriteria responseCriteria = new ResponseCriteria();
          responseCriteria.addCondition(responseCondition);

          surveyScreen.addResponseCriteria(responseCriteria, transition);
        } else if (responseCriteriaModel.getCondition() == ResponseConditionOperator.DEFAULT) {
          ResponseCriteria defaultResponseCriteria = new ResponseCriteria();
          defaultResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.DEFAULT, new AssessmentResponse()));

          surveyScreen.addResponseCriteria(defaultResponseCriteria, new DirectContentTransition(null, responseCriteriaModel.getTransition()));
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
    }
    throw new IllegalArgumentException("componentModel type " + componentModel.getClass() + " is not valid.");
  }

  private TextComponent buildTextComponent(TextModel model) {
    TextComponent questionTextComponent = (TextComponent) layoutInflater.inflate(R.layout.text_view, null);
    questionTextComponent.setText(model.getLabel());
    return questionTextComponent;
  }

  private SeekBarComponent buildSeekBarComponent(SliderModel model) {
    SeekBarComponent seekBarComponent = (SeekBarComponent) layoutInflater.inflate(R.layout.seekbar, null);
    seekBarComponent.setResponseId(model.getResponseId());
    seekBarComponent.setLeftLabelText(model.getLeftLabel());
    seekBarComponent.setRightLabelText(model.getRightLabel());
    return seekBarComponent;
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
    return datePickerComponent;
  }

  private TimePickerComponent buildTimePickerComponent(TimePickerModel model) {
    TimePickerComponent timePickerComponent = (TimePickerComponent) layoutInflater.inflate(R.layout.time_picker, null);
    timePickerComponent.setResponseId(model.getResponseId());
    return timePickerComponent;
  }

  private List<Value> buildValues(ResponseCriteriaModel responseCriteriaModel) {
    List<Value> values = new ArrayList<>();
    for (String val : responseCriteriaModel.getResponse().getValues()) {
      Value value = new Value();
      value.setValue(val);
      values.add(value);
    }
    return values;
  }
}
