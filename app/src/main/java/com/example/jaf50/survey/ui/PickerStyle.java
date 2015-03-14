package com.example.jaf50.survey.ui;

public enum PickerStyle {
  CHOOSER("chooser", true, false, "primary"),
  TEXT("text", false, true, "default"),
  CHOOSER_AND_TEXT("chooser-text", true, true, "primary");

  private String description;
  private boolean chooserInputEnabled;
  private boolean textInputEnabled;
  private String bootstrapType;

  PickerStyle(String description, boolean chooserInputEnabled, boolean textInputEnabled, String bootstrapType) {
    this.description = description;
    this.chooserInputEnabled = chooserInputEnabled;
    this.textInputEnabled = textInputEnabled;
    this.bootstrapType = bootstrapType;
  }

  public String getDescription() {
    return description;
  }

  public boolean isChooserInputEnabled() {
    return chooserInputEnabled;
  }

  public boolean isTextInputEnabled() {
    return textInputEnabled;
  }

  public String getBootstrapType() {
    return bootstrapType;
  }
}