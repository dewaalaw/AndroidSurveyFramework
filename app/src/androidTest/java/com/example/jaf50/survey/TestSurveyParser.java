package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.parser.CheckboxGroupModel;
import com.example.jaf50.survey.parser.DatePickerModel;
import com.example.jaf50.survey.parser.InputModel;
import com.example.jaf50.survey.parser.RadioGroupModel;
import com.example.jaf50.survey.parser.ResponseConditionOperator;
import com.example.jaf50.survey.parser.ResponseCriteriaModel;
import com.example.jaf50.survey.parser.SliderModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.parser.SurveyParser;
import com.example.jaf50.survey.parser.SurveyScreenModel;
import com.example.jaf50.survey.parser.TextModel;
import com.example.jaf50.survey.parser.TimePickerModel;

import java.io.IOException;

public class TestSurveyParser extends AndroidTestCase {

  public void testParse() throws IOException {
    SurveyParser surveyParser = new SurveyParser();
    SurveyModel surveyModel = surveyParser.parse(getContext().getResources().openRawResource(R.raw.test_survey));

    assertEquals("My Survey", surveyModel.getName());
    assertEquals("Description", surveyModel.getDescription());
    assertEquals(2, surveyModel.getScreens().size());

    // Screen 1
    SurveyScreenModel screen1 = surveyModel.getScreens().get(0);
    assertEquals("screen1", screen1.getId());
    assertEquals(3, screen1.getComponents().size());

    TextModel textModel1 = (TextModel) screen1.getComponents().get(0);
    RadioGroupModel radioGroupModel1 = (RadioGroupModel) screen1.getComponents().get(1);
    InputModel screen1_radioInputModel1 = radioGroupModel1.getInputs().get(0);
    InputModel screen1_radioInputModel2 = radioGroupModel1.getInputs().get(1);
    InputModel screen1_radioInputModel3 = radioGroupModel1.getInputs().get(2);
    SliderModel sliderModel1 = (SliderModel) screen1.getComponents().get(2);

    assertEquals("Nice text!", textModel1.getLabel());
    assertEquals("screen1RadioResponse", radioGroupModel1.getResponseId());
    assertEquals("radio 1", screen1_radioInputModel1.getLabel());
    assertEquals("1", screen1_radioInputModel1.getValue());
    assertEquals("radio 2", screen1_radioInputModel2.getLabel());
    assertEquals("2", screen1_radioInputModel2.getValue());
    assertEquals("radio 3", screen1_radioInputModel3.getLabel());
    assertEquals("3", screen1_radioInputModel3.getValue());
    assertEquals("sliderResponse", sliderModel1.getResponseId());

    ResponseCriteriaModel responseCriteriaModel1 = screen1.getResponseCriteria().get(0);
    assertEquals(ResponseConditionOperator.EQUALS, responseCriteriaModel1.getCondition());
    assertEquals("screen1RadioResponse", responseCriteriaModel1.getResponse().getId());
    assertEquals(1, responseCriteriaModel1.getResponse().getValues().size());
    assertEquals("1", responseCriteriaModel1.getResponse().getValues().get(0));
    assertEquals("screen2", responseCriteriaModel1.getTransition());

    // Screen 2
    SurveyScreenModel screen2 = surveyModel.getScreens().get(1);
    assertEquals("screen2", screen2.getId());
    assertEquals(4, screen2.getComponents().size());

    TextModel textModel2 = (TextModel) screen2.getComponents().get(0);
    CheckboxGroupModel checkboxGroupModel2 = (CheckboxGroupModel) screen2.getComponents().get(1);
    InputModel screen2_checkboxModel1 = checkboxGroupModel2.getInputs().get(0);
    InputModel screen2_checkboxModel2 = checkboxGroupModel2.getInputs().get(1);
    DatePickerModel datePickerModel = (DatePickerModel) screen2.getComponents().get(2);
    TimePickerModel timePickerModel = (TimePickerModel) screen2.getComponents().get(3);

    assertEquals("Screen 2 Text", textModel2.getLabel());
    assertEquals("absenceType", checkboxGroupModel2.getResponseId());
    assertEquals("Vacation", screen2_checkboxModel1.getLabel());
    assertEquals("1", screen2_checkboxModel1.getValue());
    assertEquals("Sick", screen2_checkboxModel2.getLabel());
    assertEquals("2", screen2_checkboxModel2.getValue());

    assertEquals("datePickerResponse", datePickerModel.getResponseId());
    assertEquals("timePickerResponse", timePickerModel.getResponseId());

    ResponseCriteriaModel screen2_responseCriteriaModel1 = screen2.getResponseCriteria().get(0);
    ResponseCriteriaModel screen2_responseCriteriaModel2 = screen2.getResponseCriteria().get(1);

    assertEquals(ResponseConditionOperator.CONTAINS, screen2_responseCriteriaModel1.getCondition());
    assertEquals("absenceType", screen2_responseCriteriaModel1.getResponse().getId());
    assertEquals(2, screen2_responseCriteriaModel1.getResponse().getValues().size());
    assertEquals("1", screen2_responseCriteriaModel1.getResponse().getValues().get(0));
    assertEquals("4", screen2_responseCriteriaModel1.getResponse().getValues().get(1));
    assertEquals("screen3", screen2_responseCriteriaModel1.getTransition());

    assertEquals(ResponseConditionOperator.DEFAULT, screen2_responseCriteriaModel2.getCondition());
    assertEquals("screen4", screen2_responseCriteriaModel2.getTransition());
  }
}
