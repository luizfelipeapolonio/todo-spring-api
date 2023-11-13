package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserResponseDTO;
import com.felipe.todoapi.infra.security.TokenService;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;

public class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  TokenService tokenService;

  @Autowired
  @InjectMocks
  UserService userService;

  private AutoCloseable closable;

  @BeforeEach
  void setUp() {
    this.closable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void tearDown() throws Exception {
    this.closable.close();
  }

  @Test
  @DisplayName("Should successfully create a user if it doesn't already exist and return it")
  void userRegisterSuccess() {
    UserRegisterDTO userDTO = new UserRegisterDTO("User 1", "teste1@email.com", "123456");

    User user = new User();
    user.setId("01");
    user.setName(userDTO.name());
    user.setEmail(userDTO.email());
    user.setPassword(userDTO.password());

    when(this.userRepository.findByEmail(userDTO.email())).thenReturn(Optional.empty());
    when(this.userRepository.save(any(User.class))).thenReturn(user);

    UserResponseDTO createdUser = this.userService.register(userDTO);

    assertThat(createdUser.id()).isEqualTo(user.getId());
    assertThat(createdUser.name()).isEqualTo(user.getName());
    assertThat(createdUser.email()).isEqualTo(user.getEmail());
    assertThat(createdUser.createdAt()).isEqualTo(user.getCreatedAt());

    verify(this.userRepository, times(1)).findByEmail(userDTO.email());
    verify(this.userRepository, times(1)).save(any(User.class));
  }
}
