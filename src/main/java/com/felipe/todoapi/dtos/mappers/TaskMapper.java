package com.felipe.todoapi.dtos.mappers;

import com.felipe.todoapi.dtos.TaskResponseDTO;
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
      task.getPriority(),
      task.isDone(),
      task.getCreatedAt(),
      task.getUpdatedAt()
    );
  }
}
