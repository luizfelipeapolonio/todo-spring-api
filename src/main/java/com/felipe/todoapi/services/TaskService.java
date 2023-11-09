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
import org.springframework.data.domain.Sort;
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

  public List<TaskResponseDTO> getAllUserTasks(String field, String order) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null) {
      throw new AccessDeniedException("Acesso negado");
    }

    String[] sortFilter = this.getSortFilter(field, order);

    Sort sortOrder = Sort.by(Sort.Direction.fromString(sortFilter[1]), sortFilter[0]);

    List<Task> userTasks = this.taskRepository.findAllByUserId(authUser.getId(), sortOrder);

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
    newTask.setPriority(this.taskMapper.convertPriorityLevelValue(task.priority()));
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
          taskFound.setPriority(this.taskMapper.convertPriorityLevelValue(task.priority()));
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

  private String[] getSortFilter(String field, String order) {
    String[] sort = new String[2];

    if(field.equalsIgnoreCase("title")) {
      sort[0] = "title";
    }
    if(field.equalsIgnoreCase("priority")) {
      sort[0] = "priority";
    }
    if(field.equalsIgnoreCase("createdAt")) {
      sort[0] = "createdAt";
    }
    if(field.equalsIgnoreCase("updatedAt")) {
      sort[0] = "updatedAt";
    }

    if(order.equalsIgnoreCase("asc")) {
      sort[1] = "ASC";
    } else {
      sort[1] = "DESC";
    }

    return sort;
  }
}
