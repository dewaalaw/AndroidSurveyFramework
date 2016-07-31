package com.askonthego.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CheckboxInputModel extends InputModel {

  private boolean mutuallyExclusive;
}
