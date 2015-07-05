package com.example.jaf50.survey.parser;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StudyModel {

  private WelcomeModel welcomeScreen;
  private List<SurveyModel> surveys;
}
