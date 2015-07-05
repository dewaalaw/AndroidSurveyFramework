package com.example.jaf50.survey.parser;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SurveyScreenModel {

  private String id;
  private String mainText;
  private NavigationButtonModel previous;
  private NavigationButtonModel next;
  private List<ComponentModel> components;
  private List<ResponseCriteriaModel> responseCriteria = new ArrayList<>();
}
