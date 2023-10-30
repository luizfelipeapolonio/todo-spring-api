package com.felipe.todoapi.enums;

public enum FailureResponseStatus {
  SUCCESS("Success"), ERROR("Error");

  private final String value;

  FailureResponseStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
