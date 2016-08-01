package com.askonthego.parser;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class NavigationButtonModel {

    private Boolean allowed;
    private String label;
}
