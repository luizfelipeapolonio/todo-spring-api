package com.felipe.todoapi.dtos;

import java.time.LocalDateTime;

public record UserResponseDTO(
  String id,
  String name,
  String email,
  LocalDateTime createdAt
) {}
