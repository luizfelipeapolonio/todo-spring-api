package com.felipe.todoapi.dtos;

import java.time.LocalDateTime;

public record UserRegisterResponseDTO(
  String id,
  String name,
  String email,
  LocalDateTime createdAt
) {}
