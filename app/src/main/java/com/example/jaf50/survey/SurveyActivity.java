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

    SeekBarComponent seekBarComponent = (SeekBarComponent) inflator.inflate(R.layout.seekbar, null);

    SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    fragment.addSurveyComponent(questionTextComponent);
    fragment.addSurveyComponent(radioGroupComponent);
    fragment.addSurveyComponent(checkboxGroupComponent);
    fragment.addSurveyComponent(seekBarComponent);
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }
}
