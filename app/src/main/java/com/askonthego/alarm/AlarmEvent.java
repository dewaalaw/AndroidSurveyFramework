package com.askonthego.alarm;

import java.io.Serializable;

public class AlarmEvent implements Serializable {

    public final String surveyName;

    public AlarmEvent(String surveyName) {
        this.surveyName = surveyName;
    }
}
