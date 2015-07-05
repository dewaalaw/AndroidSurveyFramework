package com.example.jaf50.survey.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WelcomeLinkModel {

  private String surveyName;
  private String label;
  private String transitionText;
  private String icon;
  private String buttonType;
}
