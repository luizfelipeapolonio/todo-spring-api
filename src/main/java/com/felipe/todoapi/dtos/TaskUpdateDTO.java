package com.felipe.todoapi.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.Length;

public record TaskUpdateDTO(
  @Nullable @Length(min = 1, max = 60) String title,
  @Nullable String description,
  @Nullable @Positive @Max(3) Integer priority,
  @Nullable Boolean isDone
) {}
