package com.felipe.todoapi.repositories;

import com.felipe.todoapi.models.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, String> {}
