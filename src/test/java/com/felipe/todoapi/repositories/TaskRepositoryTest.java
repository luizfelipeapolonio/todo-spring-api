package com.felipe.todoapi.repositories;

import com.felipe.todoapi.dtos.TaskCreateDTO;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.enums.PriorityLevel;
import com.felipe.todoapi.models.Task;
import com.felipe.todoapi.models.User;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.UUID;

@DataJpaTest
@ActiveProfiles("test")
public class TaskRepositoryTest {

  @Autowired
  EntityManager entityManager;

  @Autowired
  TaskRepository taskRepository;

  @Test
  @DisplayName("Should return all tasks belonging to the given user id")
  void findAllTasksByUserId() {
    User user1 = this.createUser(new UserRegisterDTO("User 1","teste1@email.com","123456"));
    User user2 = this.createUser(new UserRegisterDTO("User 2","teste2@email.com","123456"));

    this.createTask(new TaskCreateDTO("Task 1","Descrição 1","baixa"), false, user1);
    this.createTask(new TaskCreateDTO("Task 2","Descrição 2","alta"), false, user1);
    this.createTask(new TaskCreateDTO("Task 3","Descrição 3","media"), false, user2);

    List<Task> tasks = this.taskRepository.findAllByUserId(user1.getId(), Sort.by("title").ascending());

    assertThat(tasks)
      .allSatisfy(task -> assertThat(task.getUser().getId()).isEqualTo(user1.getId()))
      .hasSize(2);
  }

  @Test
  @DisplayName("Should return all tasks done or not done belonging to the given user id, according to the given task status")
  void findAllDoneOrNotDoneTasksByUserId() {
    User user1 = this.createUser(new UserRegisterDTO("User 1","teste1@email.com","123456"));
    User user2 = this.createUser(new UserRegisterDTO("User 2","teste2@email.com","123456"));

    this.createTask(new TaskCreateDTO("Task 1","Descrição 1","baixa"), true, user1);
    this.createTask(new TaskCreateDTO("Task 2","Descrição 2","alta"), true, user1);
    this.createTask(new TaskCreateDTO("Task 3","Descrição 3","media"), false, user2);

    List<Task> tasks = this.taskRepository.findAllDoneOrNotDone(user1.getId(), true);

    assertThat(tasks).allSatisfy(task -> {
      assertThat(task.getUser().getId()).isEqualTo(user1.getId());
      assertThat(task.isDone()).isEqualTo(true);
    }).hasSize(2);
  }

  private void createTask(TaskCreateDTO data, boolean isTaskDone, User user) {
    UUID randomUUID = UUID.randomUUID();
    String id = randomUUID.toString();

    Task newTask = new Task();
    newTask.setId(id);
    newTask.setTitle(data.title());
    newTask.setDescription(data.description());
    newTask.setPriority(this.convertPriorityLevelValue(data.priority()));
    newTask.setIsDone(isTaskDone);
    newTask.setUser(user);

    this.entityManager.merge(newTask);
  }

  private User createUser(UserRegisterDTO data) {
    UUID randomUUID = UUID.randomUUID();
    String id = randomUUID.toString();

    User newUser = new User();
    newUser.setId(id);
    newUser.setName(data.name());
    newUser.setEmail(data.email());
    newUser.setPassword(data.password());

    this.entityManager.merge(newUser);

    return newUser;
  }

  private PriorityLevel convertPriorityLevelValue(String value) {
    if(value == null) {
      return null;
    }

    return switch(value) {
      case "baixa" -> PriorityLevel.LOW;
      case "media" -> PriorityLevel.MEDIUM;
      case "alta" -> PriorityLevel.HIGH;
      default -> throw new IllegalArgumentException("Prioridade inválida: " + value);
    };
  }
}
