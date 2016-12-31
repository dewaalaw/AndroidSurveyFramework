package com.askonthego.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AssessmentResponse {

    private String responseId;
    private List<Object> values = new ArrayList<>();
    private Date responseDate;

    public AssessmentResponse() {
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setValues(List<Object> values) {
        this.values = values;
    }

    public List<Object> getValues() {
        return values;
    }

    public void addValue(Object value) {
        this.values.add(value);
    }

    public void setResponseDate(Date responseDate) {
        this.responseDate = responseDate;
    }

    public Date getResponseDate() {
        return responseDate;
    }

    public boolean isEmpty() {
        return getValues().isEmpty();
    }

    public boolean containsAllResponses(AssessmentResponse otherResponse) {
        if (otherResponse == null) {
            return false;
        }

        String thisResponseId = getResponseId();
        String otherResponseId = otherResponse.getResponseId();
        if (thisResponseId != null ? !thisResponseId.equals(otherResponseId) : otherResponseId != null) {
            return false;
        }

        return getValues().containsAll(otherResponse.getValues());
    }

    public boolean containsAnyResponses(AssessmentResponse otherResponse) {
        if (otherResponse == null) {
            return false;
        }

        String thisResponseId = getResponseId();
        String otherResponseId = otherResponse.getResponseId();
        if (thisResponseId != null ? !thisResponseId.equals(otherResponseId) : otherResponseId != null) {
            return false;
        }

        List<Object> otherValues = otherResponse.getValues();
        for (Object otherValue : otherValues) {
            if (getValues().contains(otherValue)) {
                return true;
            }
        }

        return false;
    }

    public boolean equalsResponse(AssessmentResponse otherResponse) {
        if (this == otherResponse) return true;
        if (otherResponse == null || getClass() != otherResponse.getClass()) return false;

        String thisResponseId = getResponseId();
        String otherResponseId = otherResponse.getResponseId();
        if (thisResponseId != null ? !thisResponseId.equals(otherResponseId) : otherResponseId != null) {
            return false;
        }

        List<Object> theseValues = getValues();
        List<Object> otherValues = otherResponse.getValues();
        if (theseValues != null ? !theseValues.equals(otherValues) : otherValues != null) {
            return false;
        }

        return true;
    }
}
