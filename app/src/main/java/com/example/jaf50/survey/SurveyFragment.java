package com.example.jaf50.survey;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndSurveyAction;
import com.example.jaf50.survey.domain.Survey;
import com.example.jaf50.survey.domain.SurveyResponse;
import com.example.jaf50.survey.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class SurveyFragment extends Fragment {

  @InjectView(R.id.contentPanel)
  LinearLayout contentPanel;

  @InjectView(R.id.nextButton)
  Button nextButton;

  @InjectView(R.id.previousButton)
  Button previousButton;

  private OnFragmentInteractionListener mListener;
  private Survey currentSurvey;
  private HashMap<String, SurveyScreen> surveyScreens = new HashMap<String, SurveyScreen>();
  private SurveyScreen currentScreen;
  private Stack<SurveyScreen> screenStack = new Stack<SurveyScreen>();

  public SurveyFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_survey, container, false);
    ButterKnife.inject(this, view);

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Action action = currentScreen.getAction();
        if (action != null) {
          if (action instanceof DirectContentTransition) {
            // TODO - collect responses for the previous screen.

            String toScreenId = ((DirectContentTransition) action).getToId();
            setCurrentScreen(toScreenId);
            SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
            screenStack.push(surveyScreen);
          } else if (action instanceof EndSurveyAction) {
            EndSurveyAction endSurveyAction = (EndSurveyAction) action;
            //endSurveyAction.setSurveyResponses(collectResponses());
            endSurveyAction.execute();
          }
        }
      }
    });

    previousButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (screenStack.size() >= 2) {
          // Remove the current screen from the top of the stack.
          screenStack.pop();
          // Then peek to get the previous screen.
          SurveyScreen previousSurveyScreen = screenStack.peek();
          setCurrentScreen(previousSurveyScreen.getScreenId());
        }
      }
    });

    return view;
  }

  private List<SurveyResponse> collectResponses(Survey survey) {
    List<SurveyResponse> surveyResponses = new ArrayList<>();

    SurveyScreen [] screensCopy = new SurveyScreen[screenStack.size()];
    screenStack.copyInto(screensCopy);

//    for (SurveyScreen screen : screensCopy) {
//      List<Response> responses = screen.getResponses();
//      for (Response response : responses) {
//        SurveyResponse surveyResponse = new SurveyResponse();
//        surveyResponse.setResponseId(response.getId());
//        surveyResponse.setSurvey(survey);
//        surveyResponse.setValues(response.getValues());
//      }
//    }

    for (SurveyScreen screen : screensCopy) {
      surveyResponses.addAll(screen.getResponses());
    }

    return surveyResponses;
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    try {
      mListener = (OnFragmentInteractionListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString()
          + " must implement OnFragmentInteractionListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public List<Response> getResponses() {
    return null;
  }

  public void addSurveyScreen(SurveyScreen surveyScreen) {
    // TODO - throw exception if this has already been added as a warning of possible duplicate ids.
    surveyScreens.put(surveyScreen.getScreenId(), surveyScreen);
  }

  public void startSurvey(String startScreenId) {
    // TODO - any other setup upon survey start (e.g. capture start timestamp).
    setCurrentScreen(startScreenId);
    SurveyScreen startSurveyScreen = surveyScreens.get(startScreenId);
    screenStack.push(startSurveyScreen);
  }

  public void setCurrentScreen(String screenId) {
    SurveyScreen surveyScreen = surveyScreens.get(screenId);
    if (surveyScreen == null) {
      throw new IllegalArgumentException("Invalid survey screen id specified: '" + screenId + "'.");
    }
    currentScreen = surveyScreen;
    contentPanel.removeAllViews();
    contentPanel.addView(surveyScreen);
  }

  public void setCurrentSurvey(Survey currentSurvey) {
    this.currentSurvey = currentSurvey;
  }

  public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(Uri uri);
  }
}
