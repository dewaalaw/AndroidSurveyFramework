package com.example.jaf50.survey.ui;

public enum DatePickerStyle {
  CALENDAR("calendar", true, false, "primary"),
  TEXT("text", false, true, "default"),
  CALENDAR_AND_TEXT("calendar-text", true, true, "primary");

  private String description;
  private boolean calendarInputEnabled;
  private boolean textInputEnabled;
  private String bootstrapType;

  DatePickerStyle(String description, boolean calendarInputEnabled, boolean textInputEnabled, String bootstrapType) {
    this.description = description;
    this.calendarInputEnabled = calendarInputEnabled;
    this.textInputEnabled = textInputEnabled;
    this.bootstrapType = bootstrapType;
  }

  public String getDescription() {
    return description;
  }

  public boolean isCalendarInputEnabled() {
    return calendarInputEnabled;
  }

  public boolean isTextInputEnabled() {
    return textInputEnabled;
  }

  public String getBootstrapType() {
    return bootstrapType;
  }
}