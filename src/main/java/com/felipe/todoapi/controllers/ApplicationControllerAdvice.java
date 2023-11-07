package com.felipe.todoapi.controllers;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
import com.felipe.todoapi.exceptions.UserAlreadyExistsException;
import com.felipe.todoapi.utils.CustomResponseBody;
import com.felipe.todoapi.utils.CustomValidationErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class ApplicationControllerAdvice {

  @ExceptionHandler(RecordNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public CustomResponseBody<Void> handleNotFoundException(RecordNotFoundException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.NOT_FOUND);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler({UsernameNotFoundException.class, BadCredentialsException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleAuthenticationException(Exception e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.UNAUTHORIZED);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(JWTVerificationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleJWTVerificationException(JWTVerificationException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.UNAUTHORIZED);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(JWTCreationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CustomResponseBody<Void> handleJWTCreationException(JWTCreationException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.BAD_REQUEST);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(AccessDeniedException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public CustomResponseBody<Void> handleAccessDeniedException(AccessDeniedException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.FORBIDDEN);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(InsufficientAuthenticationException.class)
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  public CustomResponseBody<Void> handleAuthenticationException() {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.UNAUTHORIZED);
    responseBody.setMessage("Autenticação é necessária para acessar este recurso");
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public CustomResponseBody<Void> handleEmailAlreadyExistsException(UserAlreadyExistsException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.CONFLICT);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public CustomResponseBody<List<CustomValidationErrors>> handleMethodArgumentNotValidException(
    MethodArgumentNotValidException e
  ) {
    List<CustomValidationErrors> errors = e.getBindingResult()
      .getFieldErrors()
      .stream()
      .map(fieldError -> new CustomValidationErrors(
        fieldError.getField(),
        fieldError.getRejectedValue(),
        fieldError.getDefaultMessage()
      )).toList();

    CustomResponseBody<List<CustomValidationErrors>> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.UNPROCESSABLE_ENTITY);
    responseBody.setMessage("Erros de validação");
    responseBody.setData(errors);

    return responseBody;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CustomResponseBody<Void> handleHttpMessageNotReadableException() {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.BAD_REQUEST);
    responseBody.setMessage("O tipo de dado de algum campo provido é inválido ou inconsistente");
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(IllegalArgumentException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public CustomResponseBody<Void> handleIllegalArgumentException(IllegalArgumentException e) {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.BAD_REQUEST);
    responseBody.setMessage(e.getMessage());
    responseBody.setData(null);

    return responseBody;
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public CustomResponseBody<Void> handleUncaughtException() {
    CustomResponseBody<Void> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.ERROR);
    responseBody.setCode(HttpStatus.INTERNAL_SERVER_ERROR);
    responseBody.setMessage("Ocorreu um erro interno do servidor");
    responseBody.setData(null);

    return responseBody;
  }
}
