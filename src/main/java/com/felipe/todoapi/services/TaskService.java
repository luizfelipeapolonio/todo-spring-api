package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.mappers.TaskMapper;
import com.felipe.todoapi.models.Task;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.TaskRepository;
import com.felipe.todoapi.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

  private final TaskRepository taskRepository;
  private final UserRepository userRepository;
  private final TaskMapper taskMapper;

  public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper) {
    this.taskRepository = taskRepository;
    this.userRepository = userRepository;
    this.taskMapper = taskMapper;
  }

  public List<TaskResponseDTO> getAllUserTasks() {
    // TODO: Buscar por usuário autenticado
    List<Task> userTasks = this.userRepository.findById("f6094ee7-f8ac-4f4c-9e1b-b0a777e95ff8")
      .map(User::getTasks)
      .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

    return userTasks.stream().map(this.taskMapper::toDTO).toList();
  }

  public TaskResponseDTO create(@Valid @NotNull TaskCreateDTO task) {
    // TODO: Buscar por usuário autenticado
    return this.userRepository.findById("f6094ee7-f8ac-4f4c-9e1b-b0a777e95ff8")
      .map(recordFound -> {
        Task newTask = new Task();
        newTask.setTitle(task.title());
        newTask.setDescription(task.description());
        newTask.setPriority(task.priority());
        newTask.setUser(recordFound);

        Task createdTask = this.taskRepository.save(newTask);

        return this.taskMapper.toDTO(createdTask);
      }).orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
  }
}
