package com.askonthego.alarm;

import android.content.ContextWrapper;

import com.askonthego.RegisterActivity;
import com.buzzbox.mob.android.scheduler.NotificationMessage;
import com.buzzbox.mob.android.scheduler.Task;
import com.buzzbox.mob.android.scheduler.TaskResult;

public class SurveyNotificationTask implements Task {

    @Override
    public TaskResult doWork(ContextWrapper contextWrapper) {
        TaskResult result = new TaskResult();
        NotificationMessage message = new NotificationMessage(
            "Complete a Survey", "Please complete a survey")
            .setNotificationClickIntentClass(RegisterActivity.class);
        result.addMessage(message);

        return result;
    }

    @Override
    public String getTitle() {
        return "Beep!";
    }

    @Override
    public String getId() {
        return "Beep";
    }
}
