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
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("/api")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping("/task")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<List<TaskResponseDTO>> getAllUserTasks(
    @RequestParam(defaultValue = "createdat", name = "field")
    @Pattern(regexp = "title|priority|createdat|updatedat", message = "Os parâmetros aceitos são: title, priority, createdat, updatedat")
    String field,
    @RequestParam(defaultValue = "asc", name = "order")
    @Pattern(regexp = "asc|desc", message = "Os parâmetros aceitos são: asc, desc")
    String order
  ) {
    List<TaskResponseDTO> tasks = this.taskService.getAllUserTasks(field, order);

    CustomResponseBody<List<TaskResponseDTO>> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Todas as tarefas do usuário");
    responseBody.setData(tasks);

    return responseBody;
  }

  @GetMapping("/task/done")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<List<TaskResponseDTO>> getAllDoneOrUndoneTasks(
    @RequestParam(defaultValue = "true", name = "status")
    @Pattern(regexp = "true|false", message = "Os parâmetros aceitos são: true, false")
    String status
  ) {
    List<TaskResponseDTO> tasks = this.taskService.getAllDoneOrNotDoneTasks(status);

    String message = status.equals("true") ? "Tarefas marcadas como feitas" : "Tarefas marcadas como não feitas";

    CustomResponseBody<List<TaskResponseDTO>> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage(message);
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
