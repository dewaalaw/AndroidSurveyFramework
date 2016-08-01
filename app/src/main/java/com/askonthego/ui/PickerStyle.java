package com.askonthego.ui;

import lombok.Getter;

public enum PickerStyle {
    CHOOSER("chooser", true, false, "primary"),
    TEXT("text", false, true, "default"),
    CHOOSER_AND_TEXT("chooser-text", true, true, "primary");

    @Getter private String description;
    @Getter private boolean chooserInputEnabled;
    @Getter private boolean textInputEnabled;
    @Getter private String bootstrapType;

    PickerStyle(String description, boolean chooserInputEnabled, boolean textInputEnabled, String bootstrapType) {
        this.description = description;
        this.chooserInputEnabled = chooserInputEnabled;
        this.textInputEnabled = textInputEnabled;
        this.bootstrapType = bootstrapType;
    }
}