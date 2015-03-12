package com.example.jaf50.survey.response;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeResponse {

  private Date date;
  private SimpleDateFormat twelveHourFormatter = new SimpleDateFormat("h:mm a");
  private SimpleDateFormat twentyFourHourFormatter = new SimpleDateFormat("HH:mm");
  private boolean displayInTwelveHourFormat = true;

  public TimeResponse(Date date) {
    this.date = date;
  }

  public boolean displayInTwelveHourFormat() {
    return displayInTwelveHourFormat;
  }

  public void setDisplayInTwelveHourFormat(boolean displayInTwelveHourFormat) {
    this.displayInTwelveHourFormat = displayInTwelveHourFormat;
  }

  @Override
  public String toString() {
    return getDisplayValue();
  }

  public String getDisplayValue() {
    if (displayInTwelveHourFormat()) {
      return twelveHourFormatter.format(date);
    } else {
      return twentyFourHourFormatter.format(date);
    }
  }
}
