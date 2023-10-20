package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.LoginDTO;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserRegisterResponseDTO;
import com.felipe.todoapi.infra.security.TokenService;
import com.felipe.todoapi.infra.security.UserSpringSecurity;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

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

  // ******** REMOVER ********
  public List<User> list() {
    return this.userRepository.findAll();
  }

  public UserRegisterResponseDTO register(@Valid @NotNull UserRegisterDTO user) {
    if(this.userRepository.findByEmail(user.email()).isPresent()) {
      throw new RuntimeException("E-mail já cadastrado!");
    }

    String encryptedPassword = new BCryptPasswordEncoder().encode(user.password());

    User newUser = new User(user.name(), user.email(), encryptedPassword);
    User createdUser = this.userRepository.save(newUser);

    return new UserRegisterResponseDTO(
      createdUser.getId(),
      createdUser.getName(),
      createdUser.getEmail(),
      createdUser.getCreatedAt()
    );
  }

  public String login(@Valid @NotNull LoginDTO login) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var token = this.tokenService.generateToken((UserSpringSecurity) auth.getPrincipal());

    return token;
  }
}
