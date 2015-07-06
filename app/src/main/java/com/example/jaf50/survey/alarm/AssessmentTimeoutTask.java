package com.example.jaf50.survey.alarm;

import android.content.ContextWrapper;
import android.content.Intent;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.example.jaf50.survey.AssessmentHolder;
import com.example.jaf50.survey.SurveyActivity;
import com.example.jaf50.survey.util.LogUtils;

public class AssessmentTimeoutTask implements Task {

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      LogUtils.d(getClass(), "In doWork()...");
      if (AssessmentHolder.getInstance().isAssessmentInProgress()) {
        LogUtils.d(getClass(), "In doWork(), assessmentInProgress block.");
        Intent intent = new Intent(contextWrapper, SurveyActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra("isTimeout", true);
        contextWrapper.startActivity(intent);
      } else {
        LogUtils.d(getClass(), "No assessment is currently in progress so the timeout task is not notifying the SurveyActivity.");
      }
    } catch (Exception e) {
      LogUtils.d(getClass(), "In doWork(), exception occurred: " + e);
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
