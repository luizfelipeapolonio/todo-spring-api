package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.LoginDTO;
import com.felipe.todoapi.dtos.LoginResponseDTO;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserResponseDTO;
import com.felipe.todoapi.exceptions.RecordNotFoundException;
import com.felipe.todoapi.exceptions.UserAlreadyExistsException;
import com.felipe.todoapi.infra.security.TokenService;
import com.felipe.todoapi.infra.security.UserSpringSecurity;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.anyString;

public class UserServiceTest {

  @Mock
  UserRepository userRepository;

  @Mock
  AuthenticationManager authenticationManager;

  @Mock
  Authentication authentication;

  @Mock
  SecurityContext securityContext;

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
    SecurityContextHolder.clearContext();
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

  @Test
  @DisplayName("Should throw a UserAlreadyExistsException when attempt to register a user that already exists")
  void userRegisterFailWithAnExistentUser() throws UserAlreadyExistsException {
    UserRegisterDTO userDTO = new UserRegisterDTO("User 1", "teste1@email.com", "123456");

    User user = new User();
    user.setId("01");
    user.setName(userDTO.name());
    user.setEmail(userDTO.email());
    user.setPassword(userDTO.password());

    when(this.userRepository.findByEmail(userDTO.email())).thenReturn(Optional.of(user));

    Exception thrown = catchException(() -> this.userService.register(userDTO));

    assertThat(thrown)
      .isExactlyInstanceOf(UserAlreadyExistsException.class)
      .hasMessage("E-mail já cadastrado!");

    verify(this.userRepository, times(1)).findByEmail(userDTO.email());
  }

  @Test
  @DisplayName("Should log user in and return the logged in user info and an access token")
  void userLoginSuccess() {
    LoginDTO loginData = new LoginDTO("teste1@email.com", "123456");
    Authentication usernamePassword = new UsernamePasswordAuthenticationToken(loginData.email(), loginData.password());

    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail(loginData.email());
    user.setPassword(loginData.password());

    when(this.authenticationManager.authenticate(usernamePassword)).thenReturn(this.authentication);
    when(this.tokenService.generateToken(any())).thenReturn("AccessToken");
    when(this.userRepository.findByEmail(loginData.email())).thenReturn(Optional.of(user));

    LoginResponseDTO authenticatedUser = this.userService.login(loginData);

    assertThat(authenticatedUser.id()).isEqualTo(user.getId());
    assertThat(authenticatedUser.name()).isEqualTo(user.getName());
    assertThat(authenticatedUser.email()).isEqualTo(user.getEmail());
    assertThat(authenticatedUser.token()).isEqualTo("AccessToken");

    verify(this.authenticationManager, times(1)).authenticate(usernamePassword);
    verify(this.tokenService, times(1)).generateToken(any());
    verify(this.userRepository, times(1)).findByEmail(loginData.email());
  }

  @Test
  @DisplayName("Should throw a BadCredentialsException when login attempt fails due to invalid credentials")
  void userLoginFailByBadCredentials() throws BadCredentialsException {
    LoginDTO loginData = new LoginDTO("teste1@email.com", "123456");

    doThrow(BadCredentialsException.class).when(this.authenticationManager).authenticate(any());

    Exception thrown = catchException(() -> this.userService.login(loginData));

    assertThat(thrown)
      .isExactlyInstanceOf(BadCredentialsException.class)
      .hasMessage("Usuário ou senha inválidos");

    verify(this.authenticationManager, times(1)).authenticate(any());
    verify(this.tokenService, never()).generateToken(any());
    verify(this.userRepository, never()).findByEmail(any());
  }

  @Test
  @DisplayName("Should throw a RecordNotFoundException when provided email does not exist")
  void userLoginFailByNotFoundUser() throws RecordNotFoundException {
    LoginDTO loginData = new LoginDTO("teste1@email.com", "123456");

    when(this.authenticationManager.authenticate(any())).thenReturn(this.authentication);
    when(this.tokenService.generateToken(any())).thenReturn("AccessToken");
    when(this.userRepository.findByEmail(loginData.email())).thenReturn(Optional.empty());

    Exception thrown = catchException(() -> this.userService.login(loginData));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Usuário não encontrado");

    verify(this.userRepository, times(1)).findByEmail(loginData.email());
    verify(this.authenticationManager, times(1)).authenticate(any());
    verify(this.tokenService, times(1)).generateToken(any());
  }

  @Test
  @DisplayName("Should return authenticated user profile information")
  void getAuthUserProfileSuccess() {
    User user = new User();
    user.setId("01");
    user.setName("User 1");
    user.setEmail("teste1@email.com");
    user.setPassword("123456");

    UserSpringSecurity authUser = new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword());

    when(this.authentication.getPrincipal()).thenReturn(authUser);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    when(this.userRepository.findById(eq("01"))).thenReturn(Optional.of(user));

    SecurityContextHolder.setContext(this.securityContext);

    UserResponseDTO authUserProfile = this.userService.getAuthUserProfile(user.getId());

    assertThat(AuthorizationService.getAuthentication().getUsername()).isEqualTo(authUser.getUsername());
    assertThat(authUserProfile.id()).isEqualTo(authUser.getId());
    assertThat(authUserProfile.name()).isEqualTo(user.getName());
    assertThat(authUserProfile.email()).isEqualTo(authUser.getUsername());
    assertThat(authUserProfile.createdAt()).isEqualTo(user.getCreatedAt());

    verify(this.userRepository, times(1)).findById(eq("01"));
  }

  @Test
  @DisplayName("Should throw an AccessDeniedException when authenticated user returns null")
  void getAuthUserProfileFailByNullAuthUser() throws AccessDeniedException {
    when(this.authentication.getPrincipal()).thenReturn(null);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);

    SecurityContextHolder.setContext(this.securityContext);

    Exception thrown = catchException(() -> this.userService.getAuthUserProfile("01"));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Acesso negado!");
    assertThat(this.authentication.getPrincipal()).isNull();

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.userRepository, never()).findById(anyString());
  }

  @Test
  @DisplayName("Should throw an AccessDeniedException when the provided ID is different from the authenticated user's ID")
  void getAuthUserProfileFailByDifferentUserId() throws AccessDeniedException {
    UserSpringSecurity authUser = new UserSpringSecurity("01", "teste1@email.com", "123456");

    when(this.authentication.getPrincipal()).thenReturn(authUser);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);

    SecurityContextHolder.setContext(this.securityContext);

    Exception thrown = catchException(() -> this.userService.getAuthUserProfile("02"));

    assertThat(thrown)
      .isExactlyInstanceOf(AccessDeniedException.class)
      .hasMessage("Acesso negado!");

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.userRepository, never()).findById(anyString());
  }

  @Test
  @DisplayName("Should throw a RecordNotFoundException when the provided ID does not exist")
  void getAuthUserProfileFailByNotFoundUser() throws RecordNotFoundException {
    UserSpringSecurity authUser = new UserSpringSecurity("01", "teste1@email.com", "123456");

    when(this.authentication.getPrincipal()).thenReturn(authUser);
    when(this.securityContext.getAuthentication()).thenReturn(this.authentication);
    when(this.userRepository.findById(anyString())).thenReturn(Optional.empty());

    SecurityContextHolder.setContext(this.securityContext);

    Exception thrown = catchException(() -> this.userService.getAuthUserProfile("01"));

    assertThat(thrown)
      .isExactlyInstanceOf(RecordNotFoundException.class)
      .hasMessage("Usuário não encontrado!");

    verify(this.securityContext, times(1)).getAuthentication();
    verify(this.userRepository, times(1)).findById(anyString());
  }
}
