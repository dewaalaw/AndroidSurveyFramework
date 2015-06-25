package com.example.jaf50.survey.alarm;

import android.content.Context;
import android.os.Vibrator;

public class SurveyVibrator {

  public static void vibrate(Context context) {
    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.vibrate(new long[]{300, 300, 1000}, 0);
  }

  public static void cancelVibrate(Context context) {
    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
    vibrator.cancel();
  }
}
