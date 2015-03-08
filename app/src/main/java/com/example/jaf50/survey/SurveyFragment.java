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
import android.widget.Toast;

import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;

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
  private HashMap<String, SurveyScreen> surveyScreens = new HashMap<>();
  private SurveyScreen currentScreen;
  private Stack<SurveyScreen> screenStack = new Stack<>();

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
            String toScreenId = ((DirectContentTransition) action).getToId();
            setCurrentScreen(toScreenId);
            SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
            screenStack.push(surveyScreen);
          } else if (action instanceof EndAssessmentAction) {
            EndAssessmentAction endAssessmentAction = (EndAssessmentAction) action;
            endAssessmentAction.setAssessmentResponses(collectResponses());
            endAssessmentAction.execute();

            List<Assessment> assessments = Assessment.listAll(Assessment.class);
            Assessment savedAssessment = assessments.get(assessments.size()-1);
            Toast.makeText(getActivity(), "Saved data for survey " + savedAssessment.getName() + ", # assessments = " + assessments.size() + ", responses = " + savedAssessment.getResponses(), Toast.LENGTH_LONG).show();

            getActivity().finish();
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

  private List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();

    SurveyScreen [] screensCopy = new SurveyScreen[screenStack.size()];
    screenStack.copyInto(screensCopy);

    for (SurveyScreen screen : screensCopy) {
      assessmentResponses.addAll(screen.collectResponses());
    }

    return assessmentResponses;
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
    updateNavigationButtons();
    contentPanel.removeAllViews();
    contentPanel.addView(surveyScreen);
  }

  private void updateNavigationButtons() {
    if (currentScreen.getPreviousButtonModel().getLabel() != null) {
      setPreviousButtonLabel(currentScreen.getPreviousButtonModel().getLabel());
    }
    if (currentScreen.getNextButtonModel().getLabel() != null) {
      setNextButtonLabel(currentScreen.getNextButtonModel().getLabel());
    }

    if (currentScreen.getPreviousButtonModel().isAllowed()) {
      showPreviousButton();
    } else {
      hidePreviousButton();
    }

    if (currentScreen.getNextButtonModel().isAllowed()) {
      showNextButton();
    } else {
      hideNextButton();
    }
  }

  public void showPreviousButton() { previousButton.setVisibility(View.VISIBLE); }
  public void showNextButton() { nextButton.setVisibility(View.VISIBLE); }
  public void hidePreviousButton() { previousButton.setVisibility(View.INVISIBLE); }
  public void hideNextButton() { nextButton.setVisibility(View.INVISIBLE); }

  public void setPreviousButtonLabel(String label) {
    previousButton.setText(label);
  }

  public void setNextButtonLabel(String label) {
    nextButton.setText(label);
  }

  public interface OnFragmentInteractionListener {
    public void onFragmentInteraction(Uri uri);
  }
}
