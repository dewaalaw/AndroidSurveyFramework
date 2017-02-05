package com.askonthego.alarm;

import java.io.Serializable;

public class TimeoutEvent implements Serializable {

    public final String surveyName;

    public TimeoutEvent(String surveyName) {
        this.surveyName = surveyName;
    }
}
