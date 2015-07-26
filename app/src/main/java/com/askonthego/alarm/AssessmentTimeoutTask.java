package com.askonthego.alarm;

import android.content.ContextWrapper;
import android.content.Intent;

import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;
import com.askonthego.AssessmentHolder;
import com.askonthego.SurveyActivity;
import com.askonthego.util.LogUtils;

import javax.inject.Inject;

import io.pristine.sheath.Sheath;

public class AssessmentTimeoutTask implements Task {

  @Inject AssessmentHolder assessmentHolder;

  @Override
  public TaskResult doWork(ContextWrapper contextWrapper) {
    try {
      Sheath.inject(this);

      LogUtils.d(getClass(), "In doWork()...");
      if (assessmentHolder.isAssessmentInProgress()) {
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
