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
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;

  public UserService(
    UserRepository userRepository,
    AuthenticationManager authenticationManager,
    TokenService tokenService
  ) {
    this.userRepository = userRepository;
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  public UserResponseDTO register(@Valid @NotNull UserRegisterDTO user) throws UserAlreadyExistsException {
    if(this.userRepository.findByEmail(user.email()).isPresent()) {
      throw new UserAlreadyExistsException("E-mail já cadastrado!");
    }

      String encryptedPassword = new BCryptPasswordEncoder().encode(user.password());

      User newUser = new User(user.name(), user.email(), encryptedPassword);
      User createdUser = this.userRepository.save(newUser);

      return new UserResponseDTO(
        createdUser.getId(),
        createdUser.getName(),
        createdUser.getEmail(),
        createdUser.getCreatedAt()
      );
  }

  public LoginResponseDTO login(@Valid @NotNull LoginDTO login) throws BadCredentialsException {
    try {
      var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.password());
      var auth = this.authenticationManager.authenticate(usernamePassword);
      String token = this.tokenService.generateToken((UserSpringSecurity) auth.getPrincipal());

      return this.userRepository.findByEmail(login.email())
        .map(userFound -> new LoginResponseDTO(
          userFound.getId(),
          userFound.getName(),
          userFound.getEmail(),
          token
        )).orElseThrow(() -> new RecordNotFoundException("Usuário não encontrado"));

    } catch(BadCredentialsException e) {
      throw new BadCredentialsException("Usuário ou senha inválidos");
    }
  }

  public UserResponseDTO getAuthUserProfile(@NotNull @NotBlank String id) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null || !id.equals(authUser.getId())) {
      throw new AccessDeniedException("Acesso negado!");
    }

    return this.userRepository.findById(id)
      .map(userFound -> new UserResponseDTO(
        userFound.getId(),
        userFound.getName(),
        userFound.getEmail(),
        userFound.getCreatedAt()
      ))
      .orElseThrow(() -> new RecordNotFoundException("Usuário não encontrado!"));
  }

  public void delete(@NotNull @NotBlank String id) throws AccessDeniedException {
    UserSpringSecurity authUser = AuthorizationService.getAuthentication();

    if(authUser == null || !id.equals(authUser.getId())) {
      throw new AccessDeniedException("Acesso negado");
    }

    User user = this.userRepository.findById(id)
      .orElseThrow(() -> new RecordNotFoundException("Usuário não encontrado"));

    this.userRepository.deleteById(user.getId());
  }
}
