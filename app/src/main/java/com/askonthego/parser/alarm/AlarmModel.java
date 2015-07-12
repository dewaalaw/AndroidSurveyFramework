package com.askonthego.parser.alarm;

public class AlarmModel {

  private String scheduleExpression;
  private String surveyName;
  private int minuteRandomness;

  public String getScheduleExpression() {
    return scheduleExpression;
  }

  public void setScheduleExpression(String scheduleExpression) {
    this.scheduleExpression = scheduleExpression;
  }

  public String getSurveyName() {
    return surveyName;
  }

  public void setSurveyName(String surveyName) {
    this.surveyName = surveyName;
  }

  public int getMinuteRandomness() {
    return minuteRandomness;
  }

  public void setMinuteRandomness(int minuteRandomness) {
    this.minuteRandomness = minuteRandomness;
  }
}
