package com.felipe.todoapi.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record UserRegisterDTO(
  @NotNull @NotBlank @Length(min = 1, max = 15) String name,
  @NotNull @NotBlank String email,
  @NotNull @NotBlank @Length(min = 6) String password
) {}
