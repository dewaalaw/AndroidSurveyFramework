package com.example.jaf50.survey.parser;

public class CheckboxInputModel extends InputModel {

  private boolean mutuallyExclusive;

  public boolean isMutuallyExclusive() {
    return mutuallyExclusive;
  }

  public void setMutuallyExclusive(boolean mutuallyExclusive) {
    this.mutuallyExclusive = mutuallyExclusive;
  }
}
