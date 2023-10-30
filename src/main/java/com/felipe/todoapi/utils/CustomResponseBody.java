package com.felipe.todoapi.utils;

import com.felipe.todoapi.enums.ResponseStatus;
import org.springframework.http.HttpStatus;

public class CustomResponseBody<T> {
  private String status;
  private Integer code;
  private String message;
  private T data;

  public CustomResponseBody() {}

  public CustomResponseBody(ResponseStatus status, HttpStatus code, String message, T data) {
    this.status = status.getValue();
    this.code = code.value();
    this.message = message;
    this.data = data;
  }

  public String getStatus() {
    return this.status;
  }

  public void setStatus(ResponseStatus status) {
    this.status = status.getValue();
  }

  public Integer getCode() {
    return this.code;
  }

  public void setCode(HttpStatus code) {
    this.code = code.value();
  }

  public String getMessage() {
    return this.message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public T getData() {
    return this.data;
  }

  public void setData(T data) {
    this.data = data;
  }
}
