package com.example.jaf50.survey.ui;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.jaf50.survey.R;
import com.example.jaf50.survey.response.Response;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;

public class DatePickerComponent extends LinearLayout implements ISurveyComponent {

  private String responseId;

  private DatePickerDialog datePickerDialog;
  private BootstrapButton selectButton;
  private Date selectedDate;

  private SimpleDateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd");
  private SimpleDateFormat prettyDateFormatter = new SimpleDateFormat("EEE, MMMM d");

  private DatePickerDialog.OnDateSetListener onDateSetListener = new DatePickerDialog.OnDateSetListener() {
    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int monthOfYear, int dayOfMonth) {
      String dateText = year + "-" + padZeroes(monthOfYear + 1) + "-" + padZeroes(dayOfMonth);
      try {
        Date date = dateParser.parse(dateText);
        selectButton.setText(prettyDateFormatter.format(date));
        selectedDate = date;
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
  };

  private String padZeroes(int value) {
    return String.format("%2s", value + "").replace(' ', '0');
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    selectButton = ButterKnife.findById(this, R.id.selectButton);
    selectButton.setOnTouchListener(new OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
          if (datePickerDialog == null) {
            Calendar now = Calendar.getInstance();
            datePickerDialog = DatePickerDialog.newInstance(
                onDateSetListener,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            );
          }
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
