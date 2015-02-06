package com.example.jaf50.survey;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;

import butterknife.ButterKnife;

public class TimePickerComponent extends LinearLayout implements ISurveyComponent {

  private TimePickerDialog timePickerDialog;
  private Button selectButton;
  private TextView selectionTextView;
  private String selectedTime;

  private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
      selectedTime = padZeroes(hourOfDay) + ":" + padZeroes(minute);
      selectionTextView.setText(selectedTime);
    }
  };

  private String padZeroes(int value) {
    return String.format("%2s", value + "").replace(' ', '0');
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    selectButton = ButterKnife.findById(this, R.id.selectButton);
    selectionTextView = ButterKnife.findById(this, R.id.selectionTextView);

    selectButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          if (timePickerDialog == null) {
            Calendar now = Calendar.getInstance();
            timePickerDialog = TimePickerDialog.newInstance(
                onTimeSetListener,
                now.get(Calendar.HOUR_OF_DAY),
                now.get(Calendar.MINUTE),
                false);
          }
          Activity activity = (Activity) getContext();
          timePickerDialog.show(activity.getFragmentManager(), "TimePickerDialog");
        }
        return false;
      }
    });
  }

  public TimePickerComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response();
    if (selectedTime != null) {
      response.addValue(selectedTime);
    }
    return response;
  }

  @Override
  public View getView() {
    return this;
  }
}
