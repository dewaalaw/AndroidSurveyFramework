package com.askonthego;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.askonthego.alarm.ResourceService;
import com.askonthego.alarm.SurveyAlarmScheduler;
import com.askonthego.alarm.TimeoutEvent;
import com.askonthego.parser.StudyModel;
import com.askonthego.parser.WelcomeLinkModel;
import com.askonthego.parser.WelcomeModel;
import com.askonthego.service.StudyParser;
import com.beardedhen.androidbootstrap.BootstrapButton;

import java.util.concurrent.Callable;

import javax.inject.Inject;

import bolts.Task;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.pristine.sheath.Sheath;

public class WelcomeActivity extends FragmentActivity {

    @BindView(R.id.welcomeTextView) TextView welcomeTextView;
    @BindView(R.id.contentPanel) ViewGroup contentPanel;

    @Inject SurveyAlarmScheduler surveyAlarmScheduler;
    @Inject AssessmentHolder assessmentHolder;
    @Inject StudyParser studyParser;
    @Inject ResourceService resourceService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Sheath.inject(this);

        TimeoutEvent timeoutEvent = null;
        if (getIntent().hasExtra("timeoutEvent")) {
            timeoutEvent = (TimeoutEvent) getIntent().getSerializableExtra("timeoutEvent");
        }

        scheduleAlarms(this);
        if (assessmentHolder.isAssessmentInProgress() || getIntent().getStringExtra("surveyName") != null || timeoutEvent != null) {
            Log.d(getClass().getName(), "In onCreate(), surveyName = " + getIntent().getStringExtra("surveyName") + ", timeoutEvent = " + timeoutEvent);
            startSurveyActivity();
        } else {
            initWelcomeScreen();
        }
    }

    private void scheduleAlarms(final Context context) {
        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                surveyAlarmScheduler.scheduleAll(resourceService.getAlarmInputStream(context));
                return null;
            }
        });
    }

    private void startSurveyActivity() {
        Intent surveyIntent = new Intent(this, SurveyActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtras(getIntent());

        if (!getIntent().hasExtra("timeoutEvent") && !getIntent().hasExtra("alarmEvent")) {
            surveyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        startActivity(surveyIntent);
        finish();
    }

    private void startSurveyActivity(String surveyName) {
        Intent surveyIntent = new Intent(this, SurveyActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP)
                .putExtra("surveyName", surveyName);
        startActivity(surveyIntent);
        finish();
    }

    private void initWelcomeScreen() {
        setContentView(R.layout.survey_selection_screen);
        ButterKnife.bind(this);

        LayoutInflater inflater = LayoutInflater.from(this);
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "OpenSans-Bold.ttf");
        welcomeTextView.setTypeface(typeface);

        // Initialize the StudyModel.
        StudyModel studyModel = studyParser.getStudy(getResources().openRawResource(R.raw.demo_surveys));
        assessmentHolder.setStudyModel(studyModel);

        WelcomeModel welcomeModel = assessmentHolder.getStudyModel().getWelcomeScreen();
        welcomeTextView.setText(Html.fromHtml(welcomeModel.getText()));

        for (final WelcomeLinkModel welcomeLinkModel : welcomeModel.getLinks()) {
            BootstrapButton button = (BootstrapButton) inflater.inflate(R.layout.survey_selection_button, null);
            button.setText(welcomeLinkModel.getSurveyName());
            button.setBootstrapType(welcomeLinkModel.getButtonType());
            button.setRightIcon(welcomeLinkModel.getIcon());

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAssessmentWelcomeScreen(welcomeLinkModel);
                }
            });
            contentPanel.addView(button);
        }
    }

    private void showAssessmentWelcomeScreen(final WelcomeLinkModel welcomeLinkModel) {
        setContentView(R.layout.activity_welcome);
        TextView mainTextView = ButterKnife.findById(this, R.id.mainTextView);
        mainTextView.setText(welcomeLinkModel.getTransitionText());

        BootstrapButton previousButton = ButterKnife.findById(this, R.id.previousButton);
        if (welcomeLinkModel.getPreviousLabel() != null) {
            previousButton.setText(welcomeLinkModel.getPreviousLabel());
        }
        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWelcomeScreen();
            }
        });

        BootstrapButton nextButton = ButterKnife.findById(this, R.id.nextButton);
        if (welcomeLinkModel.getNextLabel() != null) {
            nextButton.setText(welcomeLinkModel.getNextLabel());
        }
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSurveyActivity(welcomeLinkModel.getSurveyName());
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);

        String surveyName = intent.getStringExtra("surveyName");
        Log.d(getClass().getName(), "In WelcomeActivity.onNewIntent(), surveyName = " + surveyName);

        startSurveyActivity();
    }
}
