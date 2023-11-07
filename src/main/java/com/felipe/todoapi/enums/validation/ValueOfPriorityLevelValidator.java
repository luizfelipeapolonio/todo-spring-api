package com.felipe.todoapi.enums.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.stream.Stream;

public class ValueOfPriorityLevelValidator implements ConstraintValidator<ValueOfPriorityLevel, String> {
  private List<String> acceptedValues;

  @Override
  public void initialize(ValueOfPriorityLevel annotation) {
    this.acceptedValues = Stream.of(annotation.enumClass().getEnumConstants())
      .map(Enum::toString)
      .toList();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if(value == null || value.isEmpty()) {
      return true;
    }
    return this.acceptedValues.contains(value);
  }
}
