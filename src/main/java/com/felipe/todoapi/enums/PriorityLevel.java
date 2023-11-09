package com.felipe.todoapi.enums;

public enum PriorityLevel {
  LOW("baixa", "3"), MEDIUM("media", "2"), HIGH("alta", "1");

  private final String value;
  private final String level;

  PriorityLevel(String value, String level) {
    this.value = value;
    this.level = level;
  }

  public String getValue() {
    return this.value;
  }

  public String getLevel() {
    return this.level;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
