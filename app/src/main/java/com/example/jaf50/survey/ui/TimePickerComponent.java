package com.example.jaf50.survey.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jaf50.survey.R;
import com.example.jaf50.survey.response.Response;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class TimePickerComponent extends LinearLayout implements ISurveyComponent {

  @InjectView(R.id.selectButton)
  BootstrapButton selectButton;
  @InjectView(R.id.hourEditText)
  BootstrapEditText hourEditText;
  @InjectView(R.id.minuteEditText)
  BootstrapEditText minuteEditText;
  @InjectView(R.id.amPmSpinner)
  Spinner amPmSpinner;

  private PickerStyle pickerStyle;
  private Date selectedTime;
  private String responseId;
  private TimePickerDialog timePickerDialog;
  private boolean isViewAttachedToWindow = false;
  private String label;
  private int amPmSelectionIndex;

  private TimePickerDialog.OnTimeSetListener onTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
    @Override
    public void onTimeSet(RadialPickerLayout radialPickerLayout, int hourOfDay, int minute) {
      Calendar calendar = Calendar.getInstance();
      calendar.clear();
      calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
      calendar.set(Calendar.MINUTE, minute);
      selectedTime = calendar.getTime();
      hourEditText.setText(padZeroes(calendar.get(Calendar.HOUR) == 0 ? 12 : calendar.get(Calendar.HOUR)));
      minuteEditText.setText(padZeroes(minute));
      // Spinner AM index == 1, PM index == 2.
      amPmSelectionIndex = radialPickerLayout.getIsCurrentlyAmOrPm() + 1;
      amPmSpinner.setSelection(amPmSelectionIndex);
    }
  };

  private String padZeroes(int value) {
    return String.format("%2s", value + "").replace(' ', '0');
  }

  public void setEditable(View view, boolean editable) {
    view.setFocusable(editable);
    view.setFocusableInTouchMode(editable);
    view.setEnabled(editable);
  }

  public void setPickerStyle(PickerStyle pickerStyle) {
    this.pickerStyle = pickerStyle;
    if (isViewAttachedToWindow && pickerStyle != null) {
      setSelectButtonCornersRounded(pickerStyle.isChooserInputEnabled());
      selectButton.setBootstrapType(pickerStyle.getBootstrapType());
      selectButton.setBootstrapButtonEnabled(pickerStyle.isChooserInputEnabled());
      setEditable(hourEditText, pickerStyle.isTextInputEnabled());
      setEditable(minuteEditText, pickerStyle.isTextInputEnabled());
      setEditable(amPmSpinner, pickerStyle.isTextInputEnabled());

      // Prevents the spinner from requiring one tap to initially focus the component, and another to shows the drop-down.
      amPmSpinner.setFocusableInTouchMode(false);
      amPmSpinner.setFocusable(false);
    }
  }

  private void setSelectButtonCornersRounded(boolean isRounded) {
    try {
      Field field = selectButton.getClass().getDeclaredField("roundedCorners");
      field.setAccessible(true);
      field.setBoolean(selectButton, isRounded);
    } catch (NoSuchFieldException e) {
    } catch (IllegalAccessException e) {
    }
  }

  public void setLabel(String label) {
    this.label = label;
    if (isViewAttachedToWindow) {
      selectButton.setText(label);
    }
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.isViewAttachedToWindow = true;
    ButterKnife.inject(this);

    if (this.label != null) {
      setLabel(this.label);
    }
    setPickerStyle(this.pickerStyle);

    hourEditText.setGravity(Gravity.CENTER);
    minuteEditText.setGravity(Gravity.CENTER);

    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.am_pm_array, R.layout.spinner_selected_item_layout);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    amPmSpinner.setAdapter(adapter);
    amPmSpinner.setSelection(amPmSelectionIndex);

    selectButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          Calendar calendar = Calendar.getInstance();
          if (selectedTime != null) {
            calendar.setTime(selectedTime);
          }
          timePickerDialog = TimePickerDialog.newInstance(
              onTimeSetListener,
              calendar.get(Calendar.HOUR_OF_DAY),
              calendar.get(Calendar.MINUTE),
              false);
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
    Response response = new Response(responseId);
    if (selectedTime != null) {
      response.addValue(selectedTime);
    }
    return response;
  }

  @Override
  public View getView() {
    return this;
  }

  public void setResponseId(String responseId) {
    this.responseId = responseId;
  }

  public String getResponseId() {
    return responseId;
  }
}
