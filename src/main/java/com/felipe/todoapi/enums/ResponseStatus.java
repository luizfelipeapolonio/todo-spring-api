package com.felipe.todoapi.enums;

public enum ResponseStatus {
  SUCCESS("Success"), ERROR("Error");

  private final String value;

  ResponseStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }
}
