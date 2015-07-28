package com.askonthego.alarm;

import android.content.ContextWrapper;
import android.content.Intent;

import com.askonthego.AssessmentHolder;
import com.askonthego.RegisterActivity;
import com.askonthego.util.LogUtils;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
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
        Intent intent = new Intent(contextWrapper.getApplicationContext(), RegisterActivity.class)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        EventBus.getDefault().postSticky(new TimeoutEvent());
        contextWrapper.getApplicationContext().startActivity(intent);
      } else {
        LogUtils.d(getClass(), "No assessment is currently in progress so the timeout task is not notifying the SurveyActivity.");
      }
    } catch (Exception e) {
      LogUtils.d(getClass(), "In doWork(), exception occurred: " + e);
    }
    SurveySchedulerManager.getInstance().stop(contextWrapper.getApplicationContext(), getClass());

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
