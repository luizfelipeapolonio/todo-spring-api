package com.felipe.todoapi.enums.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = ValueOfPriorityLevelValidator.class)
public @interface ValueOfPriorityLevel {
  Class<? extends Enum<?>> enumClass();
  String message() default "Os valores aceitos são: baixa, media, alta";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
