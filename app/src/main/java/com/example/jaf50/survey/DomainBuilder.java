package com.example.jaf50.survey;

import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.domain.Value;
import com.example.jaf50.survey.parser.CheckboxGroupModel;
import com.example.jaf50.survey.parser.ComponentModel;
import com.example.jaf50.survey.parser.DatePickerModel;
import com.example.jaf50.survey.parser.InputModel;
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
import com.example.jaf50.survey.ui.CheckboxGroupComponent;
import com.example.jaf50.survey.ui.DatePickerComponent;
import com.example.jaf50.survey.ui.RadioGroupComponent;
import com.example.jaf50.survey.ui.SeekBarComponent;
import com.example.jaf50.survey.ui.TextComponent;
import com.example.jaf50.survey.ui.TimePickerComponent;

import java.util.ArrayList;
import java.util.List;

public class DomainBuilder {

  private LayoutInflater layoutInflater;

  private Survey survey;
  private List<SurveyScreen> surveyScreens = new ArrayList<>();

  public DomainBuilder(LayoutInflater layoutInflater) {
    this.layoutInflater = layoutInflater;
  }

  public Survey getSurvey() {
    return survey;
  }

  public List<SurveyScreen> getSurveyScreens() {
    return surveyScreens;
  }

  public void build(SurveyModel surveyModel) {
    survey = new Survey();
    survey.setDescription(surveyModel.getDescription());
    survey.setName(surveyModel.getName());

    for (SurveyScreenModel surveyScreenModel : surveyModel.getScreens()) {
      SurveyScreen surveyScreen = (SurveyScreen) layoutInflater.inflate(R.layout.survey_content, null);
      surveyScreen.setScreenId(surveyScreenModel.getId());
      surveyScreen.setAssociatedSurvey(survey);

      for (ComponentModel componentModel : surveyScreenModel.getComponents()) {
        if (componentModel instanceof TextModel) {
          surveyScreen.addSurveyComponent(buildTextComponent((TextModel) componentModel));
        } else if (componentModel instanceof SliderModel) {
          surveyScreen.addSurveyComponent(buildSeekBarComponent((SliderModel) componentModel));
        } else if (componentModel instanceof CheckboxGroupModel) {
          surveyScreen.addSurveyComponent(buildCheckboxGroupComponent((CheckboxGroupModel) componentModel));
        } else if (componentModel instanceof RadioGroupModel) {
          surveyScreen.addSurveyComponent(buildRadioGroupComponent((RadioGroupModel) componentModel));
        } else if (componentModel instanceof DatePickerModel) {
          surveyScreen.addSurveyComponent(buildDatePickerComponent((DatePickerModel) componentModel));
        } else if (componentModel instanceof TimePickerModel) {
          surveyScreen.addSurveyComponent(buildTimePickerComponent((TimePickerModel) componentModel));
        }
      }

      for (ResponseCriteriaModel responseCriteriaModel : surveyScreenModel.getResponseCriteria()) {
        ResponseCriteria responseCriteria = new ResponseCriteria();

        if (responseCriteriaModel.getResponse() != null) {
          String operator = responseCriteriaModel.getCondition().getCondition();
          SurveyResponse surveyResponse = new SurveyResponse();
          surveyResponse.setResponseId(responseCriteriaModel.getResponse().getId());
          surveyResponse.setSurvey(survey);

          List<Value> values = new ArrayList<>();
          for (String val : responseCriteriaModel.getResponse().getValues()) {
            Value value = new Value();
            value.setValue(val);
            values.add(value);
          }
          surveyResponse.setValues(values);

          ResponseCondition responseCondition = new ResponseCondition(operator, surveyResponse);
          DirectContentTransition transition = new DirectContentTransition(responseCriteriaModel.getResponse().getId(), responseCriteriaModel.getTransition());
          responseCriteria.addCondition(responseCondition);

          surveyScreen.addResponseCriteria(responseCriteria, transition);
        } else {
          ResponseCriteria defaultResponseCriteria = new ResponseCriteria();
          defaultResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.DEFAULT.getCondition(), new SurveyResponse()));

          surveyScreen.addResponseCriteria(defaultResponseCriteria, new DirectContentTransition(null, responseCriteriaModel.getTransition()));
        }
      }

      surveyScreens.add(surveyScreen);
    }

  }

  private TextComponent buildTextComponent(TextModel model) {
    TextComponent questionTextComponent = (TextComponent) layoutInflater.inflate(R.layout.text_view, null);
    questionTextComponent.setText(model.getLabel());
    return questionTextComponent;
  }

  private SeekBarComponent buildSeekBarComponent(SliderModel model) {
    SeekBarComponent seekBarComponent = (SeekBarComponent) layoutInflater.inflate(R.layout.seekbar, null);
    seekBarComponent.setResponseId(model.getResponseId());
    return seekBarComponent;
  }

  private CheckboxGroupComponent buildCheckboxGroupComponent(CheckboxGroupModel model) {
    CheckboxGroupComponent checkboxGroupComponent = (CheckboxGroupComponent) layoutInflater.inflate(R.layout.checkbox_group, null);
    checkboxGroupComponent.setResponseId(model.getResponseId());
    for (InputModel inputModel : model.getInputs()) {
      CheckBox checkBox = (CheckBox) layoutInflater.inflate(R.layout.checkbox, null);
      checkBox.setText(inputModel.getLabel());
      checkboxGroupComponent.addComponent(checkBox);
    }
    return checkboxGroupComponent;
  }

  private RadioGroupComponent buildRadioGroupComponent(RadioGroupModel model) {
    RadioGroupComponent radioGroupComponent = (RadioGroupComponent) layoutInflater.inflate(R.layout.radio_group, null);
    radioGroupComponent.setResponseId(model.getResponseId());
    for (InputModel inputModel : model.getInputs()) {
      RadioButton radioButton = (RadioButton) layoutInflater.inflate(R.layout.radio_button, null);
      radioButton.setText(inputModel.getLabel());
      radioGroupComponent.addComponent(radioButton);
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
}
