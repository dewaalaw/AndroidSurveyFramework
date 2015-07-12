package com.example.jaf50.survey.alarm;

import android.content.Context;
import android.os.PowerManager;

public class WakeLocker {

  private PowerManager.WakeLock wakeLock;

  public void acquireFull(Context context) {
    acquire(context, PowerManager.FULL_WAKE_LOCK |
                     PowerManager.ACQUIRE_CAUSES_WAKEUP |
                     PowerManager.ON_AFTER_RELEASE);
  }

  public void acquirePartial(Context context) {
    acquire(context, PowerManager.PARTIAL_WAKE_LOCK |
                     PowerManager.ACQUIRE_CAUSES_WAKEUP |
                     PowerManager.ON_AFTER_RELEASE);
  }

  private void acquire(Context context, int levelAndFlags) {
    if (wakeLock != null) {
      wakeLock.release();
    }

    PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
    wakeLock = pm.newWakeLock(levelAndFlags, WakeLocker.class.getName());
    wakeLock.acquire();
  }

  public void release() {
    if (wakeLock != null) {
      wakeLock.release();
    }
    wakeLock = null;
  }
}