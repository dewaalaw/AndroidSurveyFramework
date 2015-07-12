package com.askonthego.parser;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class WelcomeModel {

  private String text;
  private List<WelcomeLinkModel> links;
}
