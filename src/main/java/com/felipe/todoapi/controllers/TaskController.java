package com.felipe.todoapi.controllers;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.TaskUpdateDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.services.TaskService;
import com.felipe.todoapi.utils.CustomResponseBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/task")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<List<TaskResponseDTO>> getAllUserTasks() {
    List<TaskResponseDTO> tasks = this.taskService.getAllUserTasks();

    CustomResponseBody<List<TaskResponseDTO>> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Todas as tarefas do usuário");
    responseBody.setData(tasks);

    return responseBody;
  }

  @PostMapping("/task")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<TaskResponseDTO> create(@RequestBody @Valid @NotNull TaskCreateDTO task) {
    TaskResponseDTO createdTask = this.taskService.create(task);

    CustomResponseBody<TaskResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.CREATED);
    responseBody.setMessage("Tarefa criada com sucesso");
    responseBody.setData(createdTask);

    return responseBody;
  }

  @GetMapping("/task/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<TaskResponseDTO> findById(@PathVariable @NotNull @NotBlank String id) {
    TaskResponseDTO task = this.taskService.findById(id);

    CustomResponseBody<TaskResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Tarefa encontrada");
    responseBody.setData(task);

    return responseBody;
  }

  @PatchMapping("/task/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<TaskResponseDTO> update(
    @PathVariable @NotNull @NotBlank String id,
    @RequestBody @Valid TaskUpdateDTO task
  ) {
    TaskResponseDTO updatedTask = this.taskService.update(id, task);

    CustomResponseBody<TaskResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Tarefa atualizada com sucesso");
    responseBody.setData(updatedTask);

    return responseBody;
  }

  @DeleteMapping("/task/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<Void> delete(@PathVariable @NotNull @NotBlank String id) {
    this.taskService.delete(id);
    return new CustomResponseBody<>(
      FailureResponseStatus.SUCCESS,
      HttpStatus.OK,
      "Tarefa excluída com sucesso",
      null
    );
  }
}
