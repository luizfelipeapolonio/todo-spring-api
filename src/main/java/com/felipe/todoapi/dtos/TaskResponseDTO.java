package com.felipe.todoapi.dtos;

import java.time.LocalDateTime;

public record TaskResponseDTO(
  String id,
  String title,
  String description,
  String priority,
  Boolean isDone,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
