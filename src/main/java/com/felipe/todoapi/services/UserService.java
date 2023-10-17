package com.felipe.todoapi.services;

import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserRegisterResponseDTO;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  // ******** REMOVER ********
  public List<User> list() {
    return this.userRepository.findAll();
  }

  public UserRegisterResponseDTO register(@Valid @NotNull UserRegisterDTO user) {
    User newUser = new User(user.name(), user.email(), user.password());
    User createdUser = this.userRepository.save(newUser);

    return new UserRegisterResponseDTO(
      createdUser.getId(),
      createdUser.getName(),
      createdUser.getEmail(),
      createdUser.getCreatedAt()
    );
  }
}
