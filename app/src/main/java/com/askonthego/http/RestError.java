package com.askonthego.http;

public class RestError {

    private int statusCode;
    private String error;
    private String message;
    private Validation validation;

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setValidation(Validation validation) {
        this.validation = validation;
    }

    public Validation getValidation() {
        return validation;
    }
}
