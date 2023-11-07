package com.felipe.todoapi.dtos;

import com.felipe.todoapi.enums.PriorityLevel;
import com.felipe.todoapi.enums.validation.ValueOfPriorityLevel;
import jakarta.annotation.Nullable;
import org.hibernate.validator.constraints.Length;

public record TaskUpdateDTO(
  @Nullable @Length(min = 1, max = 60, message = "O título deve ter entre 1 e 60 caracteres") String title,
  @Nullable String description,
  @Nullable @ValueOfPriorityLevel(enumClass = PriorityLevel.class) String priority,
  @Nullable Boolean isDone
) {}
