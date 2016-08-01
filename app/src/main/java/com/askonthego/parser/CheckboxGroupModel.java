package com.askonthego.parser;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CheckboxGroupModel extends ComponentModel {

    private String responseId;
    private List<CheckboxInputModel> inputs;
}
