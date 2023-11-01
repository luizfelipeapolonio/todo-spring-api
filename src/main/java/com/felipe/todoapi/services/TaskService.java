package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.TaskUpdateDTO;
import com.felipe.todoapi.dtos.mappers.TaskMapper;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
import com.felipe.todoapi.infra.security.UserSpringSecurity;
import com.felipe.todoapi.models.Task;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.TaskRepository;
import com.felipe.todoapi.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
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

  public List<TaskResponseDTO> getAllUserTasks() throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    List<Task> userTasks = this.taskRepository.findAllByUserId(authUser.getId());

    return userTasks.stream().map(this.taskMapper::toDTO).toList();
  }

  public TaskResponseDTO create(@Valid @NotNull TaskCreateDTO task) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    User user = this.userRepository.findById(authUser.getId())
      .orElseThrow(() -> new RecordNotFoundException("Usuário não encontrado"));

    Task newTask = new Task();
    newTask.setTitle(task.title());
    newTask.setDescription(task.description());
    newTask.setPriority(task.priority());
    newTask.setUser(user);

    Task createdTask = this.taskRepository.save(newTask);

    return this.taskMapper.toDTO(createdTask);
  }

  public TaskResponseDTO findById(@NotNull @NotBlank String id) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    Task task = this.taskRepository.findById(id)
      .orElseThrow(() -> new RecordNotFoundException("Tarefa não encontrada"));

    if(!task.getUser().getId().equals(authUser.getId())) {
      throw new AccessDeniedException("Acesso negado");
    }

    return this.taskMapper.toDTO(task);
  }

  public TaskResponseDTO update(@NotNull @NotBlank String id, @Valid TaskUpdateDTO task) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    return this.taskRepository.findById(id)
      .map(taskFound -> {
        if(!taskFound.getUser().getId().equals(authUser.getId())) {
          throw new AccessDeniedException("Acesso negado");
        }

        if(task.title() != null) {
          taskFound.setTitle(task.title());
        }

        if(task.description() != null) {
          taskFound.setDescription(task.description());
        }

        if(task.priority() != null) {
          taskFound.setPriority(task.priority());
        }

        if(task.isDone() != null) {
          taskFound.setIsDone(task.isDone());
        }

        Task updatedTask = this.taskRepository.save(taskFound);

        return this.taskMapper.toDTO(updatedTask);
      })
      .orElseThrow(() -> new RecordNotFoundException("Tarefa não encontrada"));
  }

  public void delete(@NotNull @NotBlank String id) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    Task task = this.taskRepository.findById(id)
      .orElseThrow(() -> new RecordNotFoundException("Tarefa não encontrada"));

    if(!task.getUser().getId().equals(authUser.getId())) {
      throw new AccessDeniedException("Acesso negado");
    }

    this.taskRepository.deleteById(task.getId());
  }
}
