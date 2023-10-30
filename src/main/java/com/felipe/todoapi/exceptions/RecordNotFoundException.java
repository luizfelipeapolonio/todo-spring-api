package com.felipe.todoapi.exceptions;

public class RecordNotFoundException extends RuntimeException {
  public RecordNotFoundException(String message) {
    super(message);
  }
}
