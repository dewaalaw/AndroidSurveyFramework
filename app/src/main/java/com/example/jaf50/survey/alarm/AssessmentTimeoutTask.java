package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.AssessmentHolder;
import com.example.jaf50.survey.SurveyActivity;

public class AssessmentTimeoutTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      Log.d(getClass().getName(), "In doWork()...");

      if (AssessmentHolder.getInstance().isAssessmentInProgress()) {
        Intent intent = new Intent(contextWrapper, SurveyActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("isTimeout", true);
        contextWrapper.startActivity(intent);
      } else {
        Log.d(getClass().getName(), "No assessment is currently in progress so the timeout task is not notifying the SurveyActivity.");
      }
    } catch (Exception e) {
      Log.d(getClass().getName(), "In doWork(), exception occurred: " + e);
    }
    SurveySchedulerManager.getInstance().stop(contextWrapper, getClass());

    return new TaskResult();
  }

  @Override
  public String getTitle() {
    return "Timeout Task";
  }

  @Override
  public String getId() {
    return "Timeout Task";
  }
}
