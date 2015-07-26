package com.askonthego;

import com.askonthego.parser.StudyModel;

import lombok.Getter;
import lombok.Setter;

public class AssessmentHolder {

  @Getter @Setter private StudyModel studyModel;
  @Getter @Setter private boolean assessmentInProgress;
}
