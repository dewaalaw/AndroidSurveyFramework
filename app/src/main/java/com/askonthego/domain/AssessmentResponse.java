package com.askonthego.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONObject;

import java.util.List;

@ParseClassName("AssessmentResponse")
public class AssessmentResponse extends ParseObject {

    public AssessmentResponse() {
    }

    public String getResponseId() {
        return getString("responseId");
    }

    public void setResponseId(String responseId) {
        put("responseId", responseId);
    }

    public List<Object> getValues() {
        return getList("values");
    }

    public void setValues(List<Object> values) {
        remove("values");
        if (values != null) {
            addAll("values", values);
        } else {
            put("values", JSONObject.NULL);
        }
    }

    public void addValue(Object value) {
        add("values", value);
    }

    public String getResponseDate() {
        return getString("responseDate");
    }

    public void setResponseDate(String responseDate) {
        if (responseDate != null) {
            put("responseDate", responseDate);
        } else {
            put("responseDate", JSONObject.NULL);
        }
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
