package com.felipe.todoapi.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
  public UserAlreadyExistsException(String message) {
    super(message);
  }
}
