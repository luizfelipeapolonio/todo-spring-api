package com.felipe.todoapi.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "task")
public class Task {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(length = 60, nullable = false)
  private String title;

  @Column(length = 100)
  private String description;

  @Column(nullable = false)
  private Integer priority;

  @Column(name = "is_done", nullable = false)
  private Boolean isDone = false;

  @CreationTimestamp
  @Column(name = "created_at", columnDefinition = "TIMESTAMP(3)", nullable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at", columnDefinition = "TIMESTAMP(3)", nullable = false)
  private LocalDateTime updatedAt;

  @ManyToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private User user;

  public Task() {}

  public String getId() {
    return this.id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return this.title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return this.description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getPriority() {
    return this.priority;
  }

  public void setPriority(Integer priority) {
    this.priority = priority;
  }

  public Boolean isDone() {
    return this.isDone;
  }

  public void setIsDone(Boolean isDone) {
    this.isDone = isDone;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
