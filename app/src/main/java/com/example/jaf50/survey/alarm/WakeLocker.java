package com.example.jaf50.survey.alarm;

import android.content.Context;
import android.os.PowerManager;

public abstract class WakeLocker {
  private static PowerManager.WakeLock wakeLock;

  public static void acquireFull(Context context) {
    acquire(context, PowerManager.FULL_WAKE_LOCK |
                     PowerManager.ACQUIRE_CAUSES_WAKEUP |
                     PowerManager.ON_AFTER_RELEASE);
  }

  public static void acquirePartial(Context context) {
    acquire(context, PowerManager.PARTIAL_WAKE_LOCK |
                     PowerManager.ACQUIRE_CAUSES_WAKEUP |
                     PowerManager.ON_AFTER_RELEASE);
  }

  private static void acquire(Context context, int levelAndFlags) {
    if (wakeLock != null) {
      wakeLock.release();
    }

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(levelAndFlags, WakeLocker.class.getName());
    wakeLock.acquire();
  }

  public static void release() {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = null;
  }
}