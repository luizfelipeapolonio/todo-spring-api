package com.felipe.todoapi.dtos;

public record LoginResponseDTO(
  String id,
  String name,
  String email,
  String token
) {}
