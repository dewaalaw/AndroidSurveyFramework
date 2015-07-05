package com.example.jaf50.survey.ui;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import lombok.Getter;
import lombok.Setter;

public class SurveySeekBar extends DiscreteSeekBar {

  public static final int DEFAULT_VALUE = -1;
  @Getter @Setter private int selectedValue = DEFAULT_VALUE;

  public SurveySeekBar(Context context, AttributeSet attrs) {
    super(context, attrs);
    setShouldDisplayValueIndicator(false);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    boolean touchResult = super.onTouchEvent(event);
    int actionMasked = MotionEventCompat.getActionMasked(event);
    switch (actionMasked) {
      case MotionEvent.ACTION_DOWN:
        // In the event that the user taps the bar on the thumb in the default position, consider this a response.
        setSelectedValue(getProgress());
        break;
    }
    return touchResult;
  }
}
