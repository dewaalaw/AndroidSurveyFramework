package com.example.jaf50.survey.domain;

public class DomainException extends RuntimeException {

  public DomainException() {
    super();
  }

  public DomainException(String message) {
    super(message);
  }

  public DomainException(String message, Throwable t) {
    super(message, t);
  }
}
