package com.felipe.todoapi.dtos;

import com.felipe.todoapi.enums.PriorityLevel;
import com.felipe.todoapi.enums.validation.ValueOfPriorityLevel;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;

public record TaskCreateDTO(
  @NotNull(message = "O título é obrigatório")
  @NotBlank(message = "O título não pode estar em branco")
  @Length(min = 1, max = 60, message = "O título deve ter entre 1 e 60 caracteres")
  String title,

  @Nullable String description,

  @NotNull(message = "A prioridade é obrigatória")
  @NotBlank(message = "A prioridade não deve estar em branco")
  @ValueOfPriorityLevel(enumClass = PriorityLevel.class)
  String priority
) {}
