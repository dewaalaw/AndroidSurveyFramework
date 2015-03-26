package com.example.jaf50.survey.response;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@ParseClassName("TimeResponse")
public class TimeResponse extends ParseObject {

  private SimpleDateFormat twelveHourFormatter = new SimpleDateFormat("h:mm a");
  private SimpleDateFormat twentyFourHourFormatter = new SimpleDateFormat("HH:mm");
  private boolean displayInTwelveHourFormat = true;

  public TimeResponse() {
  }

  public void setDate(Date date) {
    put("date", date);
  }

  public boolean displayInTwelveHourFormat() {
    return displayInTwelveHourFormat;
  }

  public void setDisplayInTwelveHourFormat(boolean displayInTwelveHourFormat) {
    this.displayInTwelveHourFormat = displayInTwelveHourFormat;
  }

  public Date getDate() {
    return getDate("date");
  }

  @Override
  public String toString() {
    return getDisplayValue();
  }

  public String getDisplayValue() {
    if (displayInTwelveHourFormat()) {
      return twelveHourFormatter.format(getDate());
    } else {
      return twentyFourHourFormatter.format(getDate());
    }
  }
}
