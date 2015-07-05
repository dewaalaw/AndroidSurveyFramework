package com.example.jaf50.survey.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ResponseCriteriaModel extends ComponentModel {

  private ResponseConditionOperator condition;
  private ResponseModel response;
  private String transition;
  private boolean responseRequired = true;
}
