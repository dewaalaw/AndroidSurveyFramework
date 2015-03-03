package com.example.jaf50.survey;

import android.test.AndroidTestCase;

import com.example.jaf50.survey.parser.CheckboxGroupModel;
import com.example.jaf50.survey.parser.ComponentModelDeserializer;
import com.example.jaf50.survey.parser.ComponentModel;
import com.example.jaf50.survey.parser.DatePickerModel;
import com.example.jaf50.survey.parser.RadioGroupModel;
import com.example.jaf50.survey.parser.SliderModel;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.parser.TextModel;
import com.example.jaf50.survey.parser.TimePickerModel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class TestSurveyParser extends AndroidTestCase {

  public void testParse() throws IOException {
    InputStream surveyInputStream = getContext().getResources().openRawResource(R.raw.test_survey);

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.registerTypeAdapter(ComponentModel.class, new ComponentModelDeserializer());
    Gson gson = gsonBuilder.create();

    SurveyModel surveyModel = gson.fromJson(new InputStreamReader(surveyInputStream), SurveyModel.class);
    assertEquals("My Survey", surveyModel.getName());
    assertEquals("Description", surveyModel.getDescription());
    assertEquals(2, surveyModel.getScreens().size());

    // Screen 1
    assertEquals("screen1", surveyModel.getScreens().get(0).getId());
    assertEquals(3, surveyModel.getScreens().get(0).getComponents().size());

    TextModel textModel1 = (TextModel) surveyModel.getScreens().get(0).getComponents().get(0);
    RadioGroupModel radioGroupModel1 = (RadioGroupModel) surveyModel.getScreens().get(0).getComponents().get(1);
    SliderModel sliderModel1 = (SliderModel) surveyModel.getScreens().get(0).getComponents().get(2);

    assertEquals("Nice text!", textModel1.getLabel());
    assertEquals("screen1RadioResponse", radioGroupModel1.getResponseId());
    assertEquals("radio 1", radioGroupModel1.getInputs().get(0).getLabel());
    assertEquals("1", radioGroupModel1.getInputs().get(0).getValue());
    assertEquals("radio 2", radioGroupModel1.getInputs().get(1).getLabel());
    assertEquals("2", radioGroupModel1.getInputs().get(1).getValue());
    assertEquals("radio 3", radioGroupModel1.getInputs().get(2).getLabel());
    assertEquals("3", radioGroupModel1.getInputs().get(2).getValue());
    assertEquals("sliderResponse", sliderModel1.getResponseId());

    // Screen 2
    assertEquals("screen2", surveyModel.getScreens().get(1).getId());
    assertEquals(4, surveyModel.getScreens().get(1).getComponents().size());

    TextModel textModel2 = (TextModel) surveyModel.getScreens().get(1).getComponents().get(0);
    CheckboxGroupModel checkboxGroupModel2 = (CheckboxGroupModel) surveyModel.getScreens().get(1).getComponents().get(1);
    DatePickerModel datePickerModel = (DatePickerModel) surveyModel.getScreens().get(1).getComponents().get(2);
    TimePickerModel timePickerModel = (TimePickerModel) surveyModel.getScreens().get(1).getComponents().get(3);

    assertEquals("Screen 2 Text", textModel2.getLabel());
    assertEquals("absenceType", checkboxGroupModel2.getResponseId());
    assertEquals("Vacation", checkboxGroupModel2.getInputs().get(0).getLabel());
    assertEquals("1", checkboxGroupModel2.getInputs().get(0).getValue());
    assertEquals("Sick", checkboxGroupModel2.getInputs().get(1).getLabel());
    assertEquals("2", checkboxGroupModel2.getInputs().get(1).getValue());

    assertEquals("datePickerResponse", datePickerModel.getResponseId());
    assertEquals("timePickerResponse", timePickerModel.getResponseId());
  }
}
