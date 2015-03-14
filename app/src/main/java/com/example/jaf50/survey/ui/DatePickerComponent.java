package com.example.jaf50.survey.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.example.jaf50.survey.R;
import com.example.jaf50.survey.response.Response;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DatePickerComponent extends LinearLayout implements ISurveyComponent {

  @InjectView(R.id.selectButton)
  BootstrapButton selectButton;
  @InjectView(R.id.dayEditText)
  BootstrapEditText dayEditText;
  @InjectView(R.id.monthEditText)
  BootstrapEditText monthEditText;
  @InjectView(R.id.yearEditText)
  BootstrapEditText yearEditText;

  private PickerStyle pickerStyle = PickerStyle.CHOOSER;
  private String responseId;
  private DatePickerDialog datePickerDialog;
  private Date selectedDate;
  private String label;

  private SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");

  private boolean isViewAttachedToWindow = false;

  private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
      String dateText = year + "-" + padZeroes(monthOfYear + 1) + "-" + padZeroes(dayOfMonth);
      try {
        Date date = dateParser.parse(dateText);
        yearEditText.setText(year + "");
        monthEditText.setText(padZeroes(monthOfYear + 1) + "");
        dayEditText.setText(padZeroes(dayOfMonth) + "");

        selectedDate = date;
      } catch (ParseException e) {
        e.printStackTrace();
      }
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
      setEditable(yearEditText, pickerStyle.isTextInputEnabled());
      setEditable(monthEditText, pickerStyle.isTextInputEnabled());
      setEditable(dayEditText, pickerStyle.isTextInputEnabled());
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

    yearEditText.setGravity(Gravity.CENTER);
    monthEditText.setGravity(Gravity.CENTER);
    dayEditText.setGravity(Gravity.CENTER);

    selectButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          Calendar calendar = Calendar.getInstance();
          if (selectedDate != null) {
            calendar.setTime(selectedDate);
          }
          datePickerDialog = DatePickerDialog.newInstance(
              onDateSetListener,
              calendar.get(Calendar.YEAR),
              calendar.get(Calendar.MONTH),
              calendar.get(Calendar.DAY_OF_MONTH)
          );
          Activity activity = (Activity) getContext();
          datePickerDialog.show(activity.getFragmentManager(), "DatePickerDialog");
        }
        return false;
      }
    });
  }

  public DatePickerComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public boolean acceptsResponse() {
    return true;
  }

  @Override
  public Response getResponse() {
    Response response = new Response(responseId);
    if (selectedDate != null) {
      response.addValue(selectedDate);
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
}
