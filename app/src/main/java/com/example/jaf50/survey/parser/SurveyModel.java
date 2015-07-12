package com.example.jaf50.survey.parser;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SurveyModel {

  private String description;
  private String name;
  private int timeoutMinutes;
  private List<SurveyScreenModel> screens;
}
