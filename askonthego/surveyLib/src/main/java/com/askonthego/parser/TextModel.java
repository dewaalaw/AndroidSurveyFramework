package com.askonthego.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class TextModel extends ComponentModel {

  private String label;
}
