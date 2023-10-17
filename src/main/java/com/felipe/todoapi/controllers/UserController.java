package com.felipe.todoapi.controllers;

import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserRegisterResponseDTO;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.services.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // ******** REMOVER ********
  @GetMapping("/auth/users")
  public List<User> test() {
    return this.userService.list();
  }

  @PostMapping("/auth/register")
  public ResponseEntity<UserRegisterResponseDTO> register(@RequestBody @Valid @NotNull UserRegisterDTO user) {
    UserRegisterResponseDTO newUser = this.userService.register(user);
    return ResponseEntity.ok().body(newUser);
  }
}
