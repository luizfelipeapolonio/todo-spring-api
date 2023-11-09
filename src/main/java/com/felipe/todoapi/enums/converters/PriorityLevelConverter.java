package com.felipe.todoapi.enums.converters;

import com.felipe.todoapi.enums.PriorityLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.stream.Stream;

@Converter(autoApply = true)
public class PriorityLevelConverter implements AttributeConverter<PriorityLevel, String> {

  @Override
  public String convertToDatabaseColumn(PriorityLevel priorityLevel) {
    if(priorityLevel == null) {
      return null;
    }
    return priorityLevel.getLevel();
  }

  @Override
  public PriorityLevel convertToEntityAttribute(String value) {
    if(value == null) {
      return null;
    }
    return Stream.of(PriorityLevel.values())
      .filter(priorityLevel -> priorityLevel.getLevel().equals(value))
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Valor de Enum inválido: " + value));
  }
}
