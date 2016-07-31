package com.askonthego.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

import lombok.Getter;
import lombok.Setter;


public class CheckboxComponent extends CheckBox {

  @Getter @Setter private String value;
  @Getter @Setter private boolean mutuallyExclusive;

  public CheckboxComponent(Context context, AttributeSet attrs) {
    super(context, attrs);
  }
}
