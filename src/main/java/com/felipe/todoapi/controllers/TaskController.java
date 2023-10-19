package com.felipe.todoapi.controllers;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.TaskUpdateDTO;
import com.felipe.todoapi.services.TaskService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<List<TaskResponseDTO>> getAllUserTasks() {
    List<TaskResponseDTO> tasks = this.taskService.getAllUserTasks();
    return ResponseEntity.ok().body(tasks);
  }

  @PostMapping("/task")
  public ResponseEntity<TaskResponseDTO> create(@RequestBody @Valid @NotNull TaskCreateDTO task) {
    TaskResponseDTO createdTask = this.taskService.create(task);
    return ResponseEntity.ok().body(createdTask);
  }

  @GetMapping("/task/{id}")
  public ResponseEntity<TaskResponseDTO> findById(@PathVariable @NotNull @NotBlank String id) {
    TaskResponseDTO task = this.taskService.findById(id);
    return ResponseEntity.ok().body(task);
  }

  @PatchMapping("/task/{id}")
  public ResponseEntity<TaskResponseDTO> update(
    @PathVariable @NotNull @NotBlank String id,
    @RequestBody @Valid TaskUpdateDTO task
  ) {
    TaskResponseDTO updatedTask = this.taskService.update(id, task);
    return ResponseEntity.ok().body(updatedTask);
  }

  @DeleteMapping("/task/{id}")
  public ResponseEntity<Void> delete(@PathVariable @NotNull @NotBlank String id) {
    this.taskService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
