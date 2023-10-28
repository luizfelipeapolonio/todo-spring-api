package com.felipe.todoapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

  @Value("${api.security.token.secret}")
  private String secret;

  public String generateToken(UserSpringSecurity user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(this.secret);

      return JWT.create()
        .withIssuer("todo-spring-api")
        .withSubject(user.getUsername())
        .withExpiresAt(this.generateExpirationDate())
        .sign(algorithm);
    } catch(JWTCreationException exception) {
      throw new JWTCreationException("Erro ao gerar token!", exception);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(this.secret);

      return JWT.require(algorithm)
        .withIssuer("todo-spring-api")
        .build()
        .verify(token)
        .getSubject();
    } catch(JWTVerificationException exception) {
      throw new JWTVerificationException("Token inválido");
    }
  }

  private Instant generateExpirationDate() {
    // TODO: Ajustar para 2 horas o tempo de expiração do token
    return LocalDateTime.now().plusMinutes(1).toInstant(ZoneOffset.of("-03:00"));
  }
}