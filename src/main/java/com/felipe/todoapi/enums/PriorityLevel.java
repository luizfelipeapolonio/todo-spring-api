package com.felipe.todoapi.enums;

public enum PriorityLevel {
  LOW("baixa"), MEDIUM("media"), HIGH("alta");

  private final String value;

  PriorityLevel(String value) {
    this.value = value;
  }

  public String getValue() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
