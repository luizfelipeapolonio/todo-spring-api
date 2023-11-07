package com.felipe.todoapi.dtos.mappers;

import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.enums.PriorityLevel;
import com.felipe.todoapi.models.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

  public TaskResponseDTO toDTO(Task task) {
    if(task == null) {
      return null;
    }

    return new TaskResponseDTO(
      task.getId(),
      task.getTitle(),
      task.getDescription(),
      task.getPriority().getValue(),
      task.isDone(),
      task.getCreatedAt(),
      task.getUpdatedAt()
    );
  }

  public PriorityLevel convertPriorityLevelValue(String value) {
    if(value == null) {
      return null;
    }

    return switch(value) {
      case "baixa" -> PriorityLevel.LOW;
      case "media" -> PriorityLevel.MEDIUM;
      case "alta" -> PriorityLevel.HIGH;
      default -> throw new IllegalArgumentException("Prioridade inválida: " + value);
    };
  }
}
