package com.askonthego.alarm;

import android.content.Context;
import android.os.Vibrator;

public class SurveyVibrator {

    public void vibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(new long[]{ 300, 300, 1000 }, 0);
    }

    public void cancelVibrate(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
    }
}
