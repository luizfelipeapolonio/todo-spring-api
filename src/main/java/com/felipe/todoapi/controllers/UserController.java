package com.felipe.todoapi.controllers;

import com.felipe.todoapi.dtos.LoginDTO;
import com.felipe.todoapi.dtos.LoginResponseDTO;
import com.felipe.todoapi.dtos.UserRegisterDTO;
import com.felipe.todoapi.dtos.UserResponseDTO;
import com.felipe.todoapi.enums.FailureResponseStatus;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.services.UserService;
import com.felipe.todoapi.utils.CustomResponseBody;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  // TODO: ******** REMOVER ********
  //  - Formato certo das respostas da API
  @GetMapping("/auth/users")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<List<User>> test() {
    List<User> users = this.userService.list();

    CustomResponseBody<List<User>> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Todos os usuários");
    responseBody.setData(users);

    return responseBody;
  }

  @PostMapping("/auth/register")
  @ResponseStatus(HttpStatus.CREATED)
  public CustomResponseBody<UserResponseDTO> register(@RequestBody @Valid @NotNull UserRegisterDTO user) {
    UserResponseDTO newUser = this.userService.register(user);

    CustomResponseBody<UserResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.CREATED);
    responseBody.setMessage("Usuário criado com sucesso");
    responseBody.setData(newUser);

    return responseBody;
  }

  @PostMapping("/auth/login")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<LoginResponseDTO> login(@RequestBody @Valid @NotNull LoginDTO login) {
    LoginResponseDTO loginDTO = this.userService.login(login);

    CustomResponseBody<LoginResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Usuário logado com sucesso");
    responseBody.setData(loginDTO);

    return responseBody;
  }

  @GetMapping("/profile/{id}")
  @ResponseStatus(HttpStatus.OK)
  public CustomResponseBody<UserResponseDTO> profile(@PathVariable @NotNull @NotBlank String id) {
    UserResponseDTO authUserProfile = this.userService.getAuthUserProfile(id);

    CustomResponseBody<UserResponseDTO> responseBody = new CustomResponseBody<>();
    responseBody.setStatus(FailureResponseStatus.SUCCESS);
    responseBody.setCode(HttpStatus.OK);
    responseBody.setMessage("Usuário autenticado");
    responseBody.setData(authUserProfile);

    return responseBody;
  }
}
