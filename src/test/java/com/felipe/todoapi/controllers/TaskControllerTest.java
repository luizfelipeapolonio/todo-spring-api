package com.felipe.todoapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class TaskControllerTest {

  @MockBean
  TaskService taskService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  private String baseUrl;
  private List<TaskResponseDTO> tasks;

  @BeforeEach
  void setUp() {
    this.baseUrl = "/api/task";
    this.tasks = new ArrayList<>();

    LocalDateTime DateTime1 = LocalDateTime.parse("2023-11-21T12:00:00.123456");
    LocalDateTime DateTime2 = LocalDateTime.parse("2023-11-21T13:00:00.123456");

    TaskResponseDTO task1 = new TaskResponseDTO(
      "01",
      "Tarefa 1",
      "Descrição tarefa 1",
      "baixa",
      false,
      DateTime1,
      DateTime1
    );

    TaskResponseDTO task2 = new TaskResponseDTO(
      "02",
      "Tarefa 2",
      "Descrição tarefa 2",
      "media",
      false,
      DateTime2,
      DateTime2
    );

    this.tasks.add(task1);
    this.tasks.add(task2);
  }

  @Test
  @DisplayName("Should return a success response with all authenticated user tasks")
  void getAllUserTasksSuccess() throws Exception {
    when(this.taskService.getAllUserTasks("createdat", "asc")).thenReturn(this.tasks);

    TaskResponseDTO task1 = this.tasks.get(0);
    TaskResponseDTO task2 = this.tasks.get(1);

    this.mockMvc.perform(get(this.baseUrl).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Todas as tarefas do usuário"))
      .andExpect(jsonPath("$.data[0].id").value(task1.id()))
      .andExpect(jsonPath("$.data[0].title").value(task1.title()))
      .andExpect(jsonPath("$.data[0].description").value(task1.description()))
      .andExpect(jsonPath("$.data[0].priority").value(task1.priority()))
      .andExpect(jsonPath("$.data[0].isDone").value(task1.isDone()))
      .andExpect(jsonPath("$.data[0].createdAt").value(task1.createdAt().toString()))
      .andExpect(jsonPath("$.data[0].updatedAt").value(task1.updatedAt().toString()))
      .andExpect(jsonPath("$.data[1].id").value(task2.id()))
      .andExpect(jsonPath("$.data[1].title").value(task2.title()))
      .andExpect(jsonPath("$.data[1].description").value(task2.description()))
      .andExpect(jsonPath("$.data[1].priority").value(task2.priority()))
      .andExpect(jsonPath("$.data[1].isDone").value(task2.isDone()))
      .andExpect(jsonPath("$.data[1].createdAt").value(task2.createdAt().toString()))
      .andExpect(jsonPath("$.data[1].updatedAt").value(task2.updatedAt().toString()));

    verify(this.taskService, times(1)).getAllUserTasks("createdat", "asc");
  }

  @Test
  @DisplayName("Should return an error response with a bad request status code if the request parameters are invalid")
  void getAllUserTasksFailByInvalidRequestParams() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "?field=any").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("Erros de restrição"))
      .andExpect(jsonPath("$.data[0].field").value("field"))
      .andExpect(jsonPath("$.data[0].rejectedValue").value("any"))
      .andExpect(jsonPath("$.data[0].message").value("Os parâmetros aceitos são: title, priority, createdat, updatedat"));

    this.mockMvc.perform(get(this.baseUrl + "?order=any").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("Erros de restrição"))
      .andExpect(jsonPath("$.data[0].field").value("order"))
      .andExpect(jsonPath("$.data[0].rejectedValue").value("any"))
      .andExpect(jsonPath("$.data[0].message").value("Os parâmetros aceitos são: asc, desc"));

    verify(this.taskService, never()).getAllUserTasks(anyString(), anyString());
  }
}
