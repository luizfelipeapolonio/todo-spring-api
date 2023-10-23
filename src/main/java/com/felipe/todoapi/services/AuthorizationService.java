package com.felipe.todoapi.services;

import com.felipe.todoapi.infra.security.UserSpringSecurity;
import com.felipe.todoapi.models.User;
import com.felipe.todoapi.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthorizationService implements UserDetailsService {

  private final UserRepository userRepository;

  public AuthorizationService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return this.getUserDetailsByEmail(username);
  }

  public UserDetails getUserDetailsByEmail(String email) throws UsernameNotFoundException {
    Optional<User> optionalUser = this.userRepository.findByEmail(email);

    if(optionalUser.isEmpty()) {
      throw new UsernameNotFoundException("Usuário inválido");
    }

    User user = optionalUser.get();

    return new UserSpringSecurity(user.getId(), user.getEmail(), user.getPassword());
  }

  public static UserSpringSecurity getAuthentication() {
    return (UserSpringSecurity) SecurityContextHolder
      .getContext()
      .getAuthentication()
      .getPrincipal();
  }
}
