package com.example.jaf50.survey.parser;

import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
public class SurveyModel {

  @Getter @Setter private String description;
  @Getter @Setter private String name;
  @Getter @Setter private int timeoutMinutes;
  @Getter @Setter private List<SurveyScreenModel> screens;

//  public List<String> getResponseIds() {
//    LinkedHashSet<String> responseIds = new LinkedHashSet<>();
//    for (SurveyScreenModel model : screens) {
//      if (model.getResponseCriteria().size() > 0) {
//        responseIds.add(model.getId());
//      }
//    }
//    return new ArrayList<String>(responseIds);
//  }
}
