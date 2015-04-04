package com.example.jaf50.survey;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.actions.Action;
import com.example.jaf50.survey.actions.DirectContentTransition;
import com.example.jaf50.survey.actions.EndAssessmentAction;
import com.example.jaf50.survey.domain.Assessment;
import com.example.jaf50.survey.domain.AssessmentResponse;
import com.example.jaf50.survey.parse.sdk.BetterSaveCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class SurveyFragment extends Fragment {

  @InjectView(R.id.contentPanel)
  LinearLayout contentPanel;

  @InjectView(R.id.mainTextView)
  TextView mainTextView;

  @InjectView(R.id.nextButton)
  BootstrapButton nextButton;

  @InjectView(R.id.previousButton)
  BootstrapButton previousButton;

  private HashMap<String, SurveyScreen> surveyScreens = new HashMap<>();
  private SurveyScreen currentScreen;
  private Stack<SurveyScreen> screenStack = new Stack<>();

  private Assessment currentAssessment;

  public SurveyFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_survey, container, false);
    ButterKnife.inject(this, view);

    nextButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Action action = currentScreen.getAction();
        if (action != null) {
          if (action instanceof DirectContentTransition) {
            transition((DirectContentTransition) action);
          } else if (action instanceof EndAssessmentAction) {
            previousButton.setEnabled(false);
            nextButton.setEnabled(false);
            endAssessment();
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

  private void transition(final DirectContentTransition action) {
    if (action.requiresResponse() && !currentScreen.responsesEntered()) {
      new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
          .setTitleText("Skip Question?")
          .setContentText("Would you like to skip this question?")
          .setCancelText("No")
          .setConfirmText("Yes")
          .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              // TODO - Need to mark this screen as being "skipped".
              sweetAlertDialog.dismissWithAnimation();
              transitionToNextScreen(action.getToId());
            }
          })
          .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
              sweetAlertDialog.dismissWithAnimation();
            }
          }).show();
    } else {
      transitionToNextScreen(action.getToId());
    }
  }

  private void transitionToNextScreen(String toScreenId) {
    setCurrentScreen(toScreenId);
    SurveyScreen surveyScreen = surveyScreens.get(toScreenId);
    screenStack.push(surveyScreen);
  }

  private void endAssessment() {
    currentAssessment.setResponses(collectResponses());
    currentAssessment.pinInBackground();

    currentAssessment.saveInBackground(new BetterSaveCallback() {
      @Override
      protected void onSuccess() {
        Toast.makeText(getActivity(), "Data upload succeeded: " + currentAssessment.toString(), Toast.LENGTH_LONG).show();
        getActivity().finish();
      }

      @Override
      protected void onFailure(ParseException e) {
        if (getActivity() != null) {
          Toast.makeText(getActivity(), "Data upload failed: " + e, Toast.LENGTH_LONG).show();
        }
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{"josh7up@gmail.com"});
        i.putExtra(Intent.EXTRA_SUBJECT, "Survey submission error");
        i.putExtra(Intent.EXTRA_TEXT, "Failed sending data. Stack trace = " + e);
        try {
          startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
        }
      }
    });
  }

  private List<AssessmentResponse> collectResponses() {
    List<AssessmentResponse> assessmentResponses = new ArrayList<>();

    SurveyScreen[] screensCopy = new SurveyScreen[screenStack.size()];
    screenStack.copyInto(screensCopy);

    for (SurveyScreen screen : screensCopy) {
      assessmentResponses.addAll(screen.collectResponses());
    }

    return assessmentResponses;
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
    updateMainTextView();
    contentPanel.removeAllViews();
    contentPanel.addView(surveyScreen);
  }

  public void setCurrentAssessment(Assessment currentAssessment) {
    this.currentAssessment = currentAssessment;
  }

  private void updateMainTextView() {
    mainTextView.setText(Html.fromHtml(currentScreen.getMainText()));
    mainTextView.setVisibility(TextUtils.isEmpty(currentScreen.getMainText()) ? View.INVISIBLE : View.VISIBLE);
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

  public void showPreviousButton() {
    previousButton.setVisibility(View.VISIBLE);
  }

  public void showNextButton() {
    nextButton.setVisibility(View.VISIBLE);
  }

  public void hidePreviousButton() {
    previousButton.setVisibility(View.INVISIBLE);
  }

  public void hideNextButton() {
    nextButton.setVisibility(View.INVISIBLE);
  }

  public void setPreviousButtonLabel(String label) {
    previousButton.setText(label);
  }

  public void setNextButtonLabel(String label) {
    nextButton.setText(label);
  }
}
