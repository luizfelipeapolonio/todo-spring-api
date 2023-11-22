package com.felipe.todoapi.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.felipe.todoapi.dtos.LoginDTO;
import com.felipe.todoapi.dtos.LoginResponseDTO;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserResponseDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.exceptions.UserAlreadyExistsException;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

  @MockBean
  UserService userService;

  @Autowired
  MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

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

  @Test
  @DisplayName("Should return an error response with a bad request status code if the request body is not sent")
  void registerUserFailByNullRequestBody() throws Exception {
    this.mockMvc.perform(post(this.baseUrl + "/auth/register")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("O tipo de dado de algum campo provido é inválido ou inconsistente"));

    verify(this.userService, never()).register(any(UserRegisterDTO.class));
  }

  @Test
  @DisplayName("Should return an error response with a conflict status code if user already exists")
  void registerUserFailByExistentUser() throws Exception {
    UserRegisterDTO userDTO = new UserRegisterDTO("User 1", "teste1@email.com", "123456");
    String jsonBody = this.objectMapper.writeValueAsString(userDTO);

    when(this.userService.register(any(UserRegisterDTO.class)))
      .thenThrow(new UserAlreadyExistsException("E-mail já cadastrado!"));

    this.mockMvc.perform(post(this.baseUrl + "/auth/register")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isConflict())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.CONFLICT.value()))
      .andExpect(jsonPath("$.message").value("E-mail já cadastrado!"));

    verify(this.userService, times(1)).register(any(UserRegisterDTO.class));
  }

  @Test
  @DisplayName("Should log user in successfully and return a success response with the user info and an access token")
  void userLoginSuccess() throws Exception {
    LoginDTO loginData = new LoginDTO("teste1@email.com", "123456");
    LoginResponseDTO loggedInUser = new LoginResponseDTO(
      "01",
      "User 1",
      loginData.email(),
      "AccessToken"
    );
    String jsonBody = this.objectMapper.writeValueAsString(loginData);

    when(this.userService.login(loginData)).thenReturn(loggedInUser);

    this.mockMvc.perform(post(this.baseUrl + "/auth/login")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.SUCCESS.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.OK.value()))
      .andExpect(jsonPath("$.message").value("Usuário logado com sucesso"))
      .andExpect(jsonPath("$.data.id").value(loggedInUser.id()))
      .andExpect(jsonPath("$.data.name").value(loggedInUser.name()))
      .andExpect(jsonPath("$.data.email").value(loggedInUser.email()))
      .andExpect(jsonPath("$.data.token").value(loggedInUser.token()));

    verify(this.userService, times(1)).login(loginData);
  }

  @Test
  @DisplayName("Should return an error response with a bad request status code if the request body is not sent")
  void userLoginFailByNullRequestBody() throws Exception {
    this.mockMvc.perform(post(this.baseUrl + "/auth/login")
      .contentType(MediaType.APPLICATION_JSON)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.BAD_REQUEST.value()))
      .andExpect(jsonPath("$.message").value("O tipo de dado de algum campo provido é inválido ou inconsistente"));

    verify(this.userService, never()).login(any(LoginDTO.class));
  }

  @Test
  @DisplayName("Should return an error response with an unauthorized status code due to wrong credentials")
  void userLoginFailByBadCredentials() throws Exception {
    LoginDTO loginData = new LoginDTO("teste1@email.com", "123456");
    String jsonBody = this.objectMapper.writeValueAsString(loginData);

    when(this.userService.login(any(LoginDTO.class)))
      .thenThrow(new BadCredentialsException("Usuário ou senha inválidos"));

    this.mockMvc.perform(post(this.baseUrl + "/auth/login")
      .contentType(MediaType.APPLICATION_JSON).content(jsonBody)
      .accept(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnauthorized())
      .andExpect(jsonPath("$.status").value(FailureResponseStatus.ERROR.getValue()))
      .andExpect(jsonPath("$.code").value(HttpStatus.UNAUTHORIZED.value()))
      .andExpect(jsonPath("$.message").value("Usuário ou senha inválidos"));

    verify(this.userService, times(1)).login(any(LoginDTO.class));
  }
}
