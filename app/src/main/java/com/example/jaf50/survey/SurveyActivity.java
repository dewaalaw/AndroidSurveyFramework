package com.example.jaf50.survey;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;

import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.parser.SurveyModel;
import com.example.jaf50.survey.parser.SurveyParser;

import java.util.List;

public class SurveyActivity extends FragmentActivity implements SurveyFragment.OnFragmentInteractionListener {

  private Survey survey;
  private int testScreenCount = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_survey);

    SurveyParser surveyParser = new SurveyParser();
    SurveyModel surveyModel = surveyParser.parse(getResources().openRawResource(R.raw.survey));

    LayoutInflater inflator = LayoutInflater.from(this);
    DomainBuilder domainBuilder = new DomainBuilder(inflator);
    domainBuilder.build(surveyModel);

    SurveyFragment fragment = (SurveyFragment) getSupportFragmentManager().findFragmentById(R.id.survey_fragment);
    List<SurveyScreen> screens = domainBuilder.getSurveyScreens();
    for (SurveyScreen screen : screens) {
      fragment.addSurveyScreen(screen);
    }

    fragment.startSurvey(screens.get(0).getScreenId());
  }

  @Override
  public void onFragmentInteraction(Uri uri) {
  }
}
