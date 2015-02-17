package com.example.jaf50.survey.response;

public class TimeWrapper {

  private int hourOfDay;

  private int minute;

  public TimeWrapper(int hourOfDay, int minute) {
    this.hourOfDay = hourOfDay;
    this.minute = minute;
  }

  public int getHourOfDay() {
    return hourOfDay;
  }

  public int getMinute() {
    return minute;
  }

  @Override
  public String toString() {
    return padZeroes(hourOfDay) + ":" + padZeroes(minute);
  }

  private String padZeroes(int value) {
    return String.format("%2s", value + "").replace(' ', '0');
  }
}
