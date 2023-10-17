package com.felipe.todoapi.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record TaskCreateDTO(
  @NotNull @NotBlank @Length(min = 1, max = 60) String title,
  @Nullable String description,
  @NotNull @Positive @Max(3) Integer priority
) {}
