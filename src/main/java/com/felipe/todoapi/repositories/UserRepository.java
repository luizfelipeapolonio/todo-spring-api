package com.felipe.todoapi.repositories;

import com.felipe.todoapi.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {}
