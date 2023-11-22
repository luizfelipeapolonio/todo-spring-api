package com.felipe.todoapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserResponseDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.services.UserService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @MockBean
  UserService userService;

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private String baseUrl;
  private LocalDateTime mockDateTime;

  @BeforeEach
  void setUp() {
    this.baseUrl = "/api";
    this.mockDateTime = LocalDateTime.parse("2023-11-21T12:00:00.123456");
  }

  @Test
  @DisplayName("Should register a user successfully and return it")
  void registerUserSuccess() throws Exception {
    UserRegisterDTO user = new UserRegisterDTO("User 1", "teste1@email.com", "123456");
    UserResponseDTO createdUser = new UserResponseDTO("01", user.name(), user.email(), this.mockDateTime);
    String jsonBody = this.objectMapper.writeValueAsString(user);

    when(this.userService.register(user)).thenReturn(createdUser);

    this.mockMvc.perform(post(this.baseUrl + "/auth/register")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.CREATED.value()))
      .andExpect(jsonPath("$.message").value("Usuário criado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(createdUser.id()))
      .andExpect(jsonPath("$.data.name").value(createdUser.name()))
      .andExpect(jsonPath("$.data.email").value(createdUser.email()))
      .andExpect(jsonPath("$.data.createdAt").value(createdUser.createdAt().toString()));

    verify(this.userService, times(1)).register(user);
  }
}
