package com.example.jaf50.survey.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OpenEndedModel extends ComponentModel {

  private String responseId;
}
