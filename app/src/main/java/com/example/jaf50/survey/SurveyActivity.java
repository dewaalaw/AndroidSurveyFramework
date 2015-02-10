package com.example.jaf50.survey;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.widget.CheckBox;
import android.widget.RadioButton;

public class SurveyActivity extends FragmentActivity implements SurveyFragment.OnFragmentInteractionListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    fragment.addSurveyScreen(buildScreen1());
    fragment.addSurveyScreen(buildScreen2());
    fragment.addSurveyScreen(buildTestScreen("screen3", "Screen 3", new String[]{"check 1", "check 2", "check 3"}));
    fragment.addSurveyScreen(buildTestScreen("screen4", "Screen 4", new String[]{"check haha", "cool!", "good stuff", "yet another checkbox"}));

    fragment.startSurvey("screen1");
  }

  private SurveyScreen buildScreen1() {
    LayoutInflater inflator = LayoutInflater.from(this);
    TextComponent questionTextComponent = (TextComponent) inflator.inflate(R.layout.text_view, null);
    questionTextComponent.setText("Nice text!");

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    questionTextComponent.setPadding(0, 0, 0, (int)(15 * displayMetrics.density));

    RadioGroupComponent radioGroupComponent = (RadioGroupComponent) inflator.inflate(R.layout.radio_group, null);

    RadioButton radioButton1 = (RadioButton) inflator.inflate(R.layout.radio_button, null);
    RadioButton radioButton2 = (RadioButton) inflator.inflate(R.layout.radio_button, null);
    radioButton1.setText("one");
    radioButton2.setText("two");
    radioGroupComponent.addComponent(radioButton1);
    radioGroupComponent.addComponent(radioButton2);

    radioGroupComponent.setResponseId("var5");

    CheckboxGroupComponent checkboxGroupComponent = (CheckboxGroupComponent) inflator.inflate(R.layout.checkbox_group, null);

    CheckBox checkBox1 = (CheckBox) inflator.inflate(R.layout.checkbox, null);
    CheckBox checkBox2 = (CheckBox) inflator.inflate(R.layout.checkbox, null);
    CheckBox checkBox3 = (CheckBox) inflator.inflate(R.layout.checkbox, null);
    CheckBox checkBox4 = (CheckBox) inflator.inflate(R.layout.checkbox, null);
    CheckBox checkBox5 = (CheckBox) inflator.inflate(R.layout.checkbox, null);
    CheckBox checkBox6 = (CheckBox) inflator.inflate(R.layout.checkbox, null);

    checkBox1.setText("Vacation");
    checkBox2.setText("Sick");
    checkBox3.setText("Personal Holiday");
    checkBox4.setText("Earned Time Off");
    checkBox5.setText("Pay Deduction");
    checkBox6.setText("Holiday Compensatory");

    checkboxGroupComponent.addComponent(checkBox1);
    checkboxGroupComponent.addComponent(checkBox2);
    checkboxGroupComponent.addComponent(checkBox3);
    checkboxGroupComponent.addComponent(checkBox4);
    checkboxGroupComponent.addComponent(checkBox5);
    checkboxGroupComponent.addComponent(checkBox6);

    checkboxGroupComponent.setResponseId("var1");

    SeekBarComponent seekBarComponent = (SeekBarComponent) inflator.inflate(R.layout.seekbar, null);
    seekBarComponent.setResponseId("var2");

    DatePickerComponent datePickerTextView = (DatePickerComponent) inflator.inflate(R.layout.date_picker, null);
    datePickerTextView.setResponseId("var3");

    TimePickerComponent timePickerTextView = (TimePickerComponent) inflator.inflate(R.layout.time_picker, null);
    timePickerTextView.setResponseId("var4");

    SurveyScreen screen1 = (SurveyScreen) inflator.inflate(R.layout.survey_content, null);
    screen1.setScreenId("screen1");
    screen1.addSurveyComponent(questionTextComponent);
    screen1.addSurveyComponent(radioGroupComponent);
    screen1.addSurveyComponent(checkboxGroupComponent);
    screen1.addSurveyComponent(seekBarComponent);
    screen1.addSurveyComponent(datePickerTextView);
    screen1.addSurveyComponent(timePickerTextView);

    ResponseCriteria responseCriteria1 = new ResponseCriteria();
    responseCriteria1.addCondition(new ResponseCondition("=", new Response("var5").addValue("one")));

    ResponseCriteria responseCriteria2 = new ResponseCriteria();
    responseCriteria2.addCondition(new ResponseCondition("contains", new Response("var1").addValue("Personal Holiday").addValue("Pay Deduction")));

    ResponseCriteria defaultResponseCriteria = new ResponseCriteria();
    defaultResponseCriteria.addCondition(new ResponseCondition("default", new Response()));

    screen1.addResponseCriteria(responseCriteria1, new DirectContentTransition("screen1", "screen2"));
    screen1.addResponseCriteria(responseCriteria2, new DirectContentTransition("screen1", "screen3"));
    screen1.addResponseCriteria(defaultResponseCriteria, new DirectContentTransition("screen1", "screen4"));

    return screen1;
  }

  private SurveyScreen buildScreen2() {
    LayoutInflater inflator = LayoutInflater.from(this);
    TextComponent questionTextComponent = (TextComponent) inflator.inflate(R.layout.text_view, null);
    questionTextComponent.setText("Screen two text...");

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    questionTextComponent.setPadding(0, 0, 0, (int)(15 * displayMetrics.density));

    SeekBarComponent seekBarComponent = (SeekBarComponent) inflator.inflate(R.layout.seekbar, null);

    SurveyScreen screen = (SurveyScreen) inflator.inflate(R.layout.survey_content, null);
    screen.setScreenId("screen2");
    screen.addSurveyComponent(questionTextComponent);
    screen.addSurveyComponent(seekBarComponent);

    return screen;
  }

  private SurveyScreen buildTestScreen(String screenId, String questionText, String [] checkBoxTexts) {
    LayoutInflater inflator = LayoutInflater.from(this);
    TextComponent questionTextComponent = (TextComponent) inflator.inflate(R.layout.text_view, null);
    questionTextComponent.setText(questionText);

    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
    questionTextComponent.setPadding(0, 0, 0, (int)(15 * displayMetrics.density));

    CheckboxGroupComponent checkboxGroupComponent = (CheckboxGroupComponent) inflator.inflate(R.layout.checkbox_group, null);
    for (String checkBoxText : checkBoxTexts) {
      CheckBox checkBox = (CheckBox) inflator.inflate(R.layout.checkbox, null);
      checkBox.setText(checkBoxText);
      checkboxGroupComponent.addComponent(checkBox);
    }

    SurveyScreen screen = (SurveyScreen) inflator.inflate(R.layout.survey_content, null);
    screen.setScreenId(screenId);
    screen.addSurveyComponent(questionTextComponent);
    screen.addSurveyComponent(checkboxGroupComponent);

    return screen;
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }
}
