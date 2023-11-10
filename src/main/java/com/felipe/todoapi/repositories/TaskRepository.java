package com.felipe.todoapi.repositories;

import com.felipe.todoapi.models.Task;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, String> {

  @Query("SELECT t FROM Task t WHERE t.user.id = :id")
  List<Task> findAllByUserId(@Param("id") String id, Sort sort);

  @Query("SELECT t FROM Task t WHERE t.user.id = :id AND t.isDone = :status ORDER BY t.updatedAt DESC")
  List<Task> findAllDoneOrNotDone(@Param("id") String id, @Param("status") Boolean status);
}
