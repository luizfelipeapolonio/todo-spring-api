package com.felipe.todoapi.controllers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
import com.felipe.todoapi.exceptions.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(RecordNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public String handleNotFoundException(RecordNotFoundException e) {
    return e.getMessage();
  }

  @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ProblemDetail handleAuthenticationException(Exception e) {
    ProblemDetail errorDetail = ProblemDetail
      .forStatusAndDetail(HttpStatusCode.valueOf(401), e.getMessage());
    errorDetail.setProperty("access_denied_reason", "Authentication Failure");

    return errorDetail;
  }

  @ExceptionHandler(JWTVerificationException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ProblemDetail handleJWTVerificationException(JWTVerificationException e) {
    ProblemDetail errorDetail = ProblemDetail
      .forStatusAndDetail(HttpStatusCode.valueOf(403), e.getMessage());
    errorDetail.setProperty("access_denied_reason", "Invalid token");

    return errorDetail;
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ProblemDetail handleAccessDeniedException(AccessDeniedException e) {
    ProblemDetail errorDetail = ProblemDetail
      .forStatusAndDetail(HttpStatusCode.valueOf(403), e.getMessage());
    errorDetail.setProperty("access_denied_reason", "Authorization Failure");

    return errorDetail;
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public ProblemDetail handleAuthenticationException(InsufficientAuthenticationException e) {
    ProblemDetail errorDetail = ProblemDetail
      .forStatusAndDetail(HttpStatusCode.valueOf(401), e.getMessage());
    errorDetail.setProperty("access_denied_reason", "Authentication Failure");

    return errorDetail;
  }

  // TODO: Criar exceção genérica
  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ProblemDetail handleEmailAlreadyExistsException(UserAlreadyExistsException e) {
    ProblemDetail errorDetail = ProblemDetail
      .forStatusAndDetail(HttpStatusCode.valueOf(400), e.getMessage());
    errorDetail.setProperty("access_denied_reason", "User already exists");

    return errorDetail;
  }
}
