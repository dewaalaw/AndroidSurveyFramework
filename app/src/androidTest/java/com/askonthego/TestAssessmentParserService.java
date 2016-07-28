//package com.askonthego;
//
//import android.test.AndroidTestCase;
//
//import com.askonthego.parser.CheckboxGroupModel;
//import com.askonthego.parser.DatePickerModel;
//import com.askonthego.parser.InputModel;
//import com.askonthego.parser.RadioGroupModel;
//import com.askonthego.parser.ResponseConditionOperator;
//import com.askonthego.parser.ResponseCriteriaModel;
//import com.askonthego.parser.SliderModel;
//import com.askonthego.parser.SurveyModel;
//import com.askonthego.parser.SurveyScreenModel;
//import com.askonthego.parser.TextModel;
//import com.askonthego.parser.TimePickerModel;
//import com.example.jaf50.survey.R;
//
//import java.io.IOException;
//
//public class TestAssessmentParserService extends AndroidTestCase {
//
//  public void testParse() throws IOException {
//    AssessmentParserService assessmentParserService = new AssessmentParserService();
//    SurveyModel surveyModel = assessmentParserService.parse(getContext().getResources().openRawResource(R.raw.test_survey));
//
//    assertEquals("My Survey", surveyModel.getName());
//    assertEquals("Description", surveyModel.getDescription());
//    assertEquals(2, surveyModel.getScreens().size());
//
//    // Screen 1
//    SurveyScreenModel screen1 = surveyModel.getScreens().get(0);
//    assertEquals("screen1", screen1.getId());
//    assertEquals(3, screen1.getComponents().size());
//
//    TextModel textModel1 = (TextModel) screen1.getComponents().get(0);
//    RadioGroupModel radioGroupModel1 = (RadioGroupModel) screen1.getComponents().get(1);
//    InputModel screen1_radioInputModel1 = radioGroupModel1.getInputs().get(0);
//    InputModel screen1_radioInputModel2 = radioGroupModel1.getInputs().get(1);
//    InputModel screen1_radioInputModel3 = radioGroupModel1.getInputs().get(2);
//    SliderModel sliderModel1 = (SliderModel) screen1.getComponents().get(2);
//
//    assertEquals("Nice text!", textModel1.getLabel());
//    assertEquals("screen1RadioResponse", radioGroupModel1.getResponseId());
//    assertEquals("radio 1", screen1_radioInputModel1.getLabel());
//    assertEquals("1", screen1_radioInputModel1.getValue());
//    assertEquals("radio 2", screen1_radioInputModel2.getLabel());
//    assertEquals("2", screen1_radioInputModel2.getValue());
//    assertEquals("radio 3", screen1_radioInputModel3.getLabel());
//    assertEquals("3", screen1_radioInputModel3.getValue());
//    assertEquals("sliderResponse", sliderModel1.getResponseId());
//    assertEquals("Not a lot", sliderModel1.getLeftLabel());
//    assertEquals("Very much", sliderModel1.getRightLabel());
//
//    ResponseCriteriaModel responseCriteriaModel1 = screen1.getResponseCriteria().get(0);
//    assertEquals(ResponseConditionOperator.EQUALS, responseCriteriaModel1.getCondition());
//    assertEquals("screen1RadioResponse", responseCriteriaModel1.getResponse().getId());
//    assertEquals(1, responseCriteriaModel1.getResponse().getValues().size());
//    assertEquals("1", responseCriteriaModel1.getResponse().getValues().get(0));
//    assertEquals("screen2", responseCriteriaModel1.getTransition());
//
//    // Screen 2
//    SurveyScreenModel screen2 = surveyModel.getScreens().get(1);
//    assertEquals("screen2", screen2.getId());
//    assertEquals(4, screen2.getComponents().size());
//
//    TextModel textModel2 = (TextModel) screen2.getComponents().get(0);
//    CheckboxGroupModel checkboxGroupModel2 = (CheckboxGroupModel) screen2.getComponents().get(1);
//    InputModel screen2_checkboxModel1 = checkboxGroupModel2.getInputs().get(0);
//    InputModel screen2_checkboxModel2 = checkboxGroupModel2.getInputs().get(1);
//    DatePickerModel datePickerModel = (DatePickerModel) screen2.getComponents().get(2);
//    TimePickerModel timePickerModel = (TimePickerModel) screen2.getComponents().get(3);
//
//    assertEquals("Screen 2 Text", textModel2.getLabel());
//    assertEquals("absenceType", checkboxGroupModel2.getResponseId());
//    assertEquals("Vacation", screen2_checkboxModel1.getLabel());
//    assertEquals("1", screen2_checkboxModel1.getValue());
//    assertEquals("Sick", screen2_checkboxModel2.getLabel());
//    assertEquals("2", screen2_checkboxModel2.getValue());
//
//    assertEquals("datePickerResponse", datePickerModel.getResponseId());
//    assertEquals("timePickerResponse", timePickerModel.getResponseId());
//
//    ResponseCriteriaModel screen2_responseCriteriaModel1 = screen2.getResponseCriteria().get(0);
//    ResponseCriteriaModel screen2_responseCriteriaModel2 = screen2.getResponseCriteria().get(1);
//
//    assertEquals(ResponseConditionOperator.CONTAINS_ANY, screen2_responseCriteriaModel1.getCondition());
//    assertEquals("absenceType", screen2_responseCriteriaModel1.getResponse().getId());
//    assertEquals(2, screen2_responseCriteriaModel1.getResponse().getValues().size());
//    assertEquals("1", screen2_responseCriteriaModel1.getResponse().getValues().get(0));
//    assertEquals("4", screen2_responseCriteriaModel1.getResponse().getValues().get(1));
//    assertEquals("screen3", screen2_responseCriteriaModel1.getTransition());
//
//    assertEquals(ResponseConditionOperator.DEFAULT, screen2_responseCriteriaModel2.getCondition());
//    assertEquals("screen4", screen2_responseCriteriaModel2.getTransition());
//  }
//}
