package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.TaskResponseDTO;
import com.felipe.todoapi.dtos.mappers.TaskMapper;
import com.felipe.todoapi.enums.PriorityLevel;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
import com.felipe.todoapi.infra.security.UserSpringSecurity;
import com.felipe.todoapi.models.Task;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.TaskRepository;
import com.felipe.todoapi.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.anyBoolean;


public class TaskServiceTest {

  @Mock
  TaskRepository taskRepository;

  @Mock
  UserRepository userRepository;

  @Spy
  TaskMapper taskMapper;

  @Mock
  Authentication authentication;

  @Mock
  SecurityContext securityContext;

  @Autowired
  @InjectMocks
  TaskService taskService;

  private AutoCloseable closable;

  @BeforeEach
  void setUp() {
    this.closable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closable.close();
    SecurityContextHolder.clearContext();
  }

  @Test
  @DisplayName("Should return a task list with all tasks that belong to the authenticated user")
  void getAllUserTasksSuccess() {
    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("teste1@email.com");
    user.setPassword("123456");

    UserSpringSecurity authUser = new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword());
    List<Task> tasks = this.generateTaskList(user);

    this.mockAuthentication(authUser);
    when(this.taskRepository.findAllByUserId(eq(authUser.getId()), any())).thenReturn(tasks);

    List<TaskResponseDTO> userTasks = this.taskService.getAllUserTasks("priority", "desc");

    assertThat(userTasks).hasSize(3);

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.taskRepository, times(1)).findAllByUserId(eq(authUser.getId()), any());
  }

  @Test
  @DisplayName("Should throw an AccessDeniedException when the authenticated user returns null")
  void getAllUserTasksFailByNullAuthUser() throws AccessDeniedException {
    this.mockAuthentication(null);

    Exception thrown = catchException(() -> this.taskService.getAllUserTasks("priority", "desc"));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Acesso negado");

    verify(this.taskRepository, never()).findAllByUserId(anyString(), any());
    verify(this.securityContext, times(1)).getAuthentication();
  }

  @Test
  @DisplayName("Should return all done or not done tasks of authenticated user")
  void getAllDoneOrNotDoneTasksSuccess() {
    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("teste1@email.com");
    user.setPassword("123456");

    UserSpringSecurity authUser = new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword());
    List<Task> tasks = this.generateTaskList(user);

    this.mockAuthentication(authUser);
    when(this.taskRepository.findAllDoneOrNotDone(user.getId(), false)).thenReturn(tasks);

    List<TaskResponseDTO> userTasks = this.taskService.getAllDoneOrNotDoneTasks("false");

    assertThat(userTasks).allSatisfy(taskResponseDTO -> {
      assertThat(taskResponseDTO.isDone()).isEqualTo(false);
    }).hasSize(3);

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.taskRepository, times(1)).findAllDoneOrNotDone(user.getId(), false);
  }

  @Test
  @DisplayName("Should throw an AccessDeniedException when the authenticated user returns null")
  void getAllDoneOrNotDoneTasksFailByNullAuthUser() throws AccessDeniedException {
    this.mockAuthentication(null);

    Exception thrown = catchException(() -> this.taskService.getAllDoneOrNotDoneTasks("false"));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Acesso negado");

    verify(this.taskRepository, never()).findAllDoneOrNotDone(anyString(), anyBoolean());
    verify(this.securityContext, times(1)).getAuthentication();
  }

  @Test
  @DisplayName("Should successfully create a task and return it")
  void createTaskSuccess() {
    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("teste1@email.com");
    user.setPassword("123456");

    TaskCreateDTO taskDTO = new TaskCreateDTO("Task 1", "Descrição task 1", "baixa");

    Task newTask = new Task();
    newTask.setId("01");
    newTask.setTitle(taskDTO.title());
    newTask.setDescription(taskDTO.description());
    newTask.setPriority(this.taskMapper.convertPriorityLevelValue(taskDTO.priority()));
    newTask.setIsDone(false);
    newTask.setUser(user);

    UserSpringSecurity authUser = new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword());

    this.mockAuthentication(authUser);
    when(this.userRepository.findById(authUser.getId())).thenReturn(Optional.of(user));
    when(this.taskRepository.save(any(Task.class))).thenReturn(newTask);

    TaskResponseDTO createdTask = this.taskService.create(taskDTO);

    assertThat(createdTask.id()).isEqualTo(newTask.getId());
    assertThat(createdTask.title()).isEqualTo(newTask.getTitle());
    assertThat(createdTask.description()).isEqualTo(newTask.getDescription());
    assertThat(createdTask.priority()).isEqualTo(newTask.getPriority().getValue());
    assertThat(createdTask.isDone()).isEqualTo(newTask.isDone());
    assertThat(createdTask.createdAt()).isEqualTo(newTask.getCreatedAt());
    assertThat(createdTask.updatedAt()).isEqualTo(newTask.getUpdatedAt());
    assertThat(newTask.getUser().getId()).isEqualTo(authUser.getId());

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.userRepository, times(1)).findById(authUser.getId());
    verify(this.taskRepository, times(1)).save(any(Task.class));
  }

  @Test
  @DisplayName("Should throw an AccessDeniedException when the authenticated user returns null")
  void createTaskFailByNullAuthUser() throws AccessDeniedException {
    TaskCreateDTO taskDTO = new TaskCreateDTO("Task 1", "Descrição task 1", "baixa");

    this.mockAuthentication(null);

    Exception thrown = catchException(() -> this.taskService.create(taskDTO));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Acesso negado");

    verify(this.userRepository, never()).findById(anyString());
    verify(this.taskRepository, never()).save(any(Task.class));
    verify(this.securityContext, times(1)).getAuthentication();
  }

  @Test
  @DisplayName("Should throw a RecordNotFoundException when the user does not exist")
  void createTaskFailByNotFoundUser() {
    TaskCreateDTO taskDTO = new TaskCreateDTO("Task 1", "Descrição task 1", "baixa");

    UserSpringSecurity authUser = new UserSpringSecurity("01", "teste1@email.com", "123456");

    this.mockAuthentication(authUser);
    when(this.userRepository.findById(authUser.getId())).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.taskService.create(taskDTO));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Usuário não encontrado");

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.userRepository, times(1)).findById(authUser.getId());
    verify(this.taskRepository, never()).save(any(Task.class));
  }

  private void mockAuthentication(UserSpringSecurity authUser) {
    when(this.authentication.getPrincipal()).thenReturn(authUser);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    SecurityContextHolder.setContext(this.securityContext);
  }

  private List<Task> generateTaskList(User user) {
    List<Task> tasks = new ArrayList<>();

    Task task1 = new Task();
    task1.setId("01");
    task1.setTitle("Tarefa 1");
    task1.setDescription("Descrição tarefa 1");
    task1.setPriority(PriorityLevel.LOW);
    task1.setIsDone(false);
    task1.setUser(user);

    Task task2 = new Task();
    task2.setId("02");
    task2.setTitle("Tarefa 2");
    task2.setDescription("Descrição tarefa 2");
    task2.setPriority(PriorityLevel.MEDIUM);
    task2.setIsDone(false);
    task2.setUser(user);

    Task task3 = new Task();
    task3.setId("03");
    task3.setTitle("Tarefa 3");
    task3.setDescription("Descrição tarefa 3");
    task3.setPriority(PriorityLevel.HIGH);
    task3.setIsDone(false);
    task3.setUser(user);

    tasks.add(task1);
    tasks.add(task2);
    tasks.add(task3);

    return tasks;
  }
}
