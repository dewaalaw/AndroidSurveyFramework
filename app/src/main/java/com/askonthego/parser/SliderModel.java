package com.askonthego.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SliderModel extends ComponentModel {

    private String responseId;
    private String leftLabel;
    private String rightLabel;
}
