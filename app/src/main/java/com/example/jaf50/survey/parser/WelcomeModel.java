package com.example.jaf50.survey.parser;

import java.util.List;

public class WelcomeModel {

  private String text;
  private List<WelcomeLinkModel> links;

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public List<WelcomeLinkModel> getLinks() {
    return links;
  }

  public void setLinks(List<WelcomeLinkModel> links) {
    this.links = links;
  }
}
