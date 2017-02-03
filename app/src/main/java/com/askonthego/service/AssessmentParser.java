package com.askonthego.service;

import android.content.Context;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.askonthego.R;
import com.askonthego.SurveyScreen;
import com.askonthego.actions.DirectContentTransition;
import com.askonthego.actions.EndAssessmentAction;
import com.askonthego.domain.AssessmentResponse;
import com.askonthego.parser.CheckboxGroupModel;
import com.askonthego.parser.CheckboxInputModel;
import com.askonthego.parser.ComponentModel;
import com.askonthego.parser.DatePickerModel;
import com.askonthego.parser.InputModel;
import com.askonthego.parser.NavigationButtonModel;
import com.askonthego.parser.OpenEndedModel;
import com.askonthego.parser.RadioGroupModel;
import com.askonthego.parser.ResponseConditionOperator;
import com.askonthego.parser.ResponseCriteriaModel;
import com.askonthego.parser.SliderModel;
import com.askonthego.parser.SpacerModel;
import com.askonthego.parser.SurveyModel;
import com.askonthego.parser.SurveyScreenModel;
import com.askonthego.parser.TextModel;
import com.askonthego.parser.TimePickerModel;
import com.askonthego.response.ResponseCondition;
import com.askonthego.response.ResponseCriteria;
import com.askonthego.ui.CheckboxComponent;
import com.askonthego.ui.CheckboxGroupComponent;
import com.askonthego.ui.DatePickerComponent;
import com.askonthego.ui.ISurveyComponent;
import com.askonthego.ui.OpenEndedComponent;
import com.askonthego.ui.RadioButtonComponent;
import com.askonthego.ui.RadioGroupComponent;
import com.askonthego.ui.SliderComponent;
import com.askonthego.ui.SpacerComponent;
import com.askonthego.ui.TextComponent;
import com.askonthego.ui.TimePickerComponent;

import java.util.ArrayList;
import java.util.List;

public class AssessmentParser {

    private Context context;
    private LayoutInflater layoutInflater;

    public AssessmentParser(Context context) {
        this.context= context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public List<SurveyScreen> getScreens(SurveyModel surveyModel) {
        List<SurveyScreen> surveyScreens = new ArrayList<>();
        for (SurveyScreenModel surveyScreenModel : surveyModel.getScreens()) {
            SurveyScreen surveyScreen = (SurveyScreen) layoutInflater.inflate(R.layout.survey_content, null);
            surveyScreen.setScreenId(surveyScreenModel.getId());
            surveyScreen.setMainText(surveyScreenModel.getMainText());
            surveyScreen.setPreviousButtonModel(getButtonModel(surveyScreenModel.getPrevious(), true, this.context.getString(R.string.previous_button_text)));
            surveyScreen.setNextButtonModel(getButtonModel(surveyScreenModel.getNext(), true, this.context.getString(R.string.next_button_text)));

            for (ComponentModel componentModel : surveyScreenModel.getComponents()) {
                surveyScreen.addSurveyComponent(buildComponent(componentModel));
            }

            for (ResponseCriteriaModel responseCriteriaModel : surveyScreenModel.getResponseCriteria()) {
                if (responseCriteriaModel.getCondition().isComparisonOperator()) {
                    addValueComparisonResponseCriteria(surveyScreen, responseCriteriaModel);
                } else if (responseCriteriaModel.getCondition() == ResponseConditionOperator.DEFAULT) {
                    addDefaultResponseCriteria(surveyScreen, responseCriteriaModel);
                } else if (responseCriteriaModel.getCondition() == ResponseConditionOperator.COMPLETE) {
                    addCompletionResponseCriteria(surveyScreen);
                }
            }

            surveyScreens.add(surveyScreen);
        }
        return surveyScreens;
    }

    private void addValueComparisonResponseCriteria(SurveyScreen surveyScreen, ResponseCriteriaModel responseCriteriaModel) {
        AssessmentResponse assessmentResponse = new AssessmentResponse();
        assessmentResponse.setResponseId(responseCriteriaModel.getResponse().getId());
        assessmentResponse.setValues(responseCriteriaModel.getResponse().getValues());

        ResponseCondition responseCondition = new ResponseCondition(responseCriteriaModel.getCondition(), assessmentResponse);

        // Navigate to the "transition" id if the response comparison evaluates to true.
        DirectContentTransition transition = new DirectContentTransition(responseCriteriaModel.getResponse().getId(),
            responseCriteriaModel.getTransition(),
            responseCriteriaModel.isResponseRequired());

        ResponseCriteria responseCriteria = new ResponseCriteria();
        responseCriteria.addCondition(responseCondition);

        surveyScreen.addResponseCriteria(responseCriteria, transition);
    }

    private void addDefaultResponseCriteria(SurveyScreen surveyScreen, ResponseCriteriaModel responseCriteriaModel) {
        ResponseCriteria defaultResponseCriteria = new ResponseCriteria();
        defaultResponseCriteria.setDefault(true);
        defaultResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.DEFAULT, new AssessmentResponse()));

        // Navigate to the "transition" id regardless of the response value.
        DirectContentTransition transition = new DirectContentTransition(null,
            responseCriteriaModel.getTransition(),
            responseCriteriaModel.isResponseRequired());

        surveyScreen.addResponseCriteria(defaultResponseCriteria, transition);
    }

    private void addCompletionResponseCriteria(SurveyScreen surveyScreen) {
        ResponseCriteria surveyCompleteResponseCriteria = new ResponseCriteria();
        surveyCompleteResponseCriteria.addCondition(new ResponseCondition(ResponseConditionOperator.COMPLETE, new AssessmentResponse()));

        surveyScreen.addResponseCriteria(surveyCompleteResponseCriteria, new EndAssessmentAction());
    }

    private NavigationButtonModel getButtonModel(NavigationButtonModel parsedModel, boolean defaultAllowed, String defaultLabel) {
        NavigationButtonModel navigationButtonModel = new NavigationButtonModel();
        navigationButtonModel.setAllowed(defaultAllowed);
        navigationButtonModel.setLabel(defaultLabel);

        if (parsedModel != null) {
            if (parsedModel.getAllowed() != null) {
                navigationButtonModel.setAllowed(parsedModel.getAllowed());
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
        questionTextComponent.setText(Html.fromHtml(model.getLabel()));
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
        for (CheckboxInputModel inputModel : model.getInputs()) {
            CheckboxComponent checkboxComponent = (CheckboxComponent) layoutInflater.inflate(R.layout.checkbox, null);
            checkboxComponent.setText(inputModel.getLabel());
            checkboxComponent.setValue(inputModel.getValue());
            checkboxComponent.setMutuallyExclusive(inputModel.isMutuallyExclusive());
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
