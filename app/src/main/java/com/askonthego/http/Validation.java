package com.askonthego.http;

import com.askonthego.http.Error;

import java.util.ArrayList;
import java.util.List;

public class Validation {

    private String source;
    private List<Error> errors = new ArrayList<>();

    public void setSource(String source) {
        this.source = source;
    }

    public String getSource() {
        return source;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
