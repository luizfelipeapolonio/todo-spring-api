package com.felipe.todoapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.TaskUpdateDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
      true,
      DateTime1,
      DateTime1
    );

    TaskResponseDTO task2 = new TaskResponseDTO(
      "02",
      "Tarefa 2",
      "Descrição tarefa 2",
      "media",
      true,
      DateTime2,
      DateTime2
    );

    this.tasks.add(task1);
    this.tasks.add(task2);
  }

  @Test
  @DisplayName("getAllUserTasks - Should return a success response with all authenticated user tasks")
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
  @DisplayName("getAllUserTasks - Should return an error response with a bad request status code if the request parameters are invalid")
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

  @Test
  @DisplayName("getAllUserTasks - Should return an error response with a forbidden status code due to invalid authentication")
  void getAllUserTasksFailByAccessDenied() throws Exception {
    when(this.taskService.getAllUserTasks(anyString(), anyString())).thenThrow(new AccessDeniedException("Acesso negado"));

    this.mockMvc.perform(get(this.baseUrl).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Acesso negado"));

    verify(this.taskService, times(1)).getAllUserTasks(anyString(), anyString());
  }

  @Test
  @DisplayName("getAllDoneOrNotDoneTasks - Should return a success response with all done or not done user tasks")
  void getAllDoneOrNotDoneTasksSuccess() throws Exception {
    when(this.taskService.getAllDoneOrNotDoneTasks("true")).thenReturn(this.tasks);

    TaskResponseDTO task1 = this.tasks.get(0);
    TaskResponseDTO task2 = this.tasks.get(1);

    this.mockMvc.perform(get(this.baseUrl + "/done").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Tarefas marcadas como feitas"))
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

    verify(this.taskService, times(1)).getAllDoneOrNotDoneTasks("true");
  }

  @Test
  @DisplayName("getAllDoneOrNotDoneTasks - Should return an error response with a bad request status code if the request parameter is invalid")
  void getAllDoneOrNotDoneTasksFailByInvalidRequestParam() throws Exception {
    this.mockMvc.perform(get(this.baseUrl + "/done?status=any").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("Erros de restrição"))
      .andExpect(jsonPath("$.data[0].field").value("status"))
      .andExpect(jsonPath("$.data[0].rejectedValue").value("any"))
      .andExpect(jsonPath("$.data[0].message").value("Os parâmetros aceitos são: true, false"));

    verify(this.taskService, never()).getAllDoneOrNotDoneTasks(anyString());
  }

  @Test
  @DisplayName("getAllDoneOrNotDoneTasks - Should return an error response with a forbidden status code due to invalid authentication")
  void getAllDoneOrNotDoneTasksFailByAccessDenied() throws Exception {
    when(this.taskService.getAllDoneOrNotDoneTasks(anyString())).thenThrow(new AccessDeniedException("Acesso negado"));

    this.mockMvc.perform(get(this.baseUrl + "/done").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Acesso negado"));

    verify(this.taskService, times(1)).getAllDoneOrNotDoneTasks(anyString());
  }

  @Test
  @DisplayName("createTask - Should create a task successfully and return a success response with the created task")
  void createTaskSuccess() throws Exception {
    TaskCreateDTO taskData = new TaskCreateDTO("Tarefa 1", "Descrição tarefa 1", "baixa");
    String jsonBody = this.objectMapper.writeValueAsString(taskData);

    TaskResponseDTO task = this.tasks.get(0);

    when(this.taskService.create(taskData)).thenReturn(task);

    this.mockMvc.perform(post(this.baseUrl)
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
      .andExpect(jsonPath("$.message").value("Tarefa criada com sucesso"))
      .andExpect(jsonPath("$.data.id").value(task.id()))
      .andExpect(jsonPath("$.data.title").value(task.title()))
      .andExpect(jsonPath("$.data.description").value(task.description()))
      .andExpect(jsonPath("$.data.priority").value(task.priority()))
      .andExpect(jsonPath("$.data.isDone").value(task.isDone()))
      .andExpect(jsonPath("$.data.createdAt").value(task.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(task.updatedAt().toString()));

    verify(this.taskService, times(1)).create(taskData);
  }

  @Test
  @DisplayName("createTask - Should return an error response with bad request status code if the request body is null")
  void createTaskFailByNullRequestBody() throws Exception {
    this.mockMvc.perform(post(this.baseUrl)
      .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("O tipo de dado de algum campo provido é inválido ou inconsistente"));

    verify(this.taskService, never()).create(any(TaskCreateDTO.class));
  }

  @Test
  @DisplayName("createTask - Should return an error response with a forbidden status code due to invalid authentication")
  void createTaskFailByAccessDenied() throws Exception {
    TaskCreateDTO taskData = new TaskCreateDTO("Tarefa 1", "Descrição tarefa 1", "baixa");
    String jsonBody = this.objectMapper.writeValueAsString(taskData);

    when(this.taskService.create(any(TaskCreateDTO.class))).thenThrow(new AccessDeniedException("Acesso negado"));

    this.mockMvc.perform(post(this.baseUrl)
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Acesso negado"));

    verify(this.taskService, times(1)).create(any(TaskCreateDTO.class));
  }

  @Test
  @DisplayName("createTask - Should return an error response with a not found status code when the task owner is not found")
  void createTaskFailByAuthUserNotFound() throws Exception {
    TaskCreateDTO taskData = new TaskCreateDTO("Tarefa 1", "Descrição tarefa 1", "baixa");
    String jsonBody = this.objectMapper.writeValueAsString(taskData);

    when(this.taskService.create(any(TaskCreateDTO.class)))
      .thenThrow(new RecordNotFoundException("Usuário não encontrado"));

    this.mockMvc.perform(post(this.baseUrl)
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Usuário não encontrado"));

    verify(this.taskService, times(1)).create(any(TaskCreateDTO.class));
  }

  @Test
  @DisplayName("findTaskById - Should return a success response with the task found by the provided ID")
  void findTaskByIdSuccess() throws Exception {
    TaskResponseDTO task = this.tasks.get(0);

    when(this.taskService.findById("01")).thenReturn(task);

    this.mockMvc.perform(get(this.baseUrl + "/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Tarefa encontrada"))
      .andExpect(jsonPath("$.data.id").value(task.id()))
      .andExpect(jsonPath("$.data.title").value(task.title()))
      .andExpect(jsonPath("$.data.description").value(task.description()))
      .andExpect(jsonPath("$.data.priority").value(task.priority()))
      .andExpect(jsonPath("$.data.isDone").value(task.isDone()))
      .andExpect(jsonPath("$.data.createdAt").value(task.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(task.updatedAt().toString()));

    verify(this.taskService, times(1)).findById("01");
  }

  @Test
  @DisplayName("findTaskById - Should return an error response with a forbidden status code due to invalid authentication")
  void findTaskByIdFailByAccessDenied() throws Exception {
    when(this.taskService.findById(anyString())).thenThrow(new AccessDeniedException("Acesso negado"));

    this.mockMvc.perform(get(this.baseUrl + "/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isForbidden())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.FORBIDDEN.value()))
      .andExpect(jsonPath("$.message").value("Acesso negado"));

    verify(this.taskService, times(1)).findById(anyString());
  }

  @Test
  @DisplayName("findTaskById - Should return an error response with a not found status code when the task is not found")
  void findTaskByIdFailByTaskNotFound() throws Exception {
    when(this.taskService.findById("01")).thenThrow(new RecordNotFoundException("Tarefa não encontrada"));

    this.mockMvc.perform(get(this.baseUrl + "/01").accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.NOT_FOUND.value()))
      .andExpect(jsonPath("$.message").value("Tarefa não encontrada"));

    verify(this.taskService, times(1)).findById("01");
  }

  @Test
  @DisplayName("updateTask - Should return a success response with the updated task")
  void updateTaskSuccess() throws Exception {
    TaskUpdateDTO task = new TaskUpdateDTO(
      "Tarefa 1 atualizada",
      "Descrição 1 autalizada",
      "media",
      true
    );
    String jsonBody = this.objectMapper.writeValueAsString(task);
    TaskResponseDTO updatedTask = new TaskResponseDTO(
      "03",
      task.title(),
      task.description(),
      task.priority(),
      task.isDone(),
      LocalDateTime.parse("2023-11-21T12:00:00.123456"),
      LocalDateTime.parse("2023-11-21T12:00:00.123456")
    );

    when(this.taskService.update("03", task)).thenReturn(updatedTask);

    this.mockMvc.perform(patch(this.baseUrl + "/03")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Tarefa atualizada com sucesso"))
      .andExpect(jsonPath("$.data.id").value(updatedTask.id()))
      .andExpect(jsonPath("$.data.title").value(updatedTask.title()))
      .andExpect(jsonPath("$.data.description").value(updatedTask.description()))
      .andExpect(jsonPath("$.data.priority").value(updatedTask.priority()))
      .andExpect(jsonPath("$.data.isDone").value(updatedTask.isDone()))
      .andExpect(jsonPath("$.data.createdAt").value(updatedTask.createdAt().toString()))
      .andExpect(jsonPath("$.data.updatedAt").value(updatedTask.updatedAt().toString()));

    verify(this.taskService, times(1)).update("03", task);
  }
}
