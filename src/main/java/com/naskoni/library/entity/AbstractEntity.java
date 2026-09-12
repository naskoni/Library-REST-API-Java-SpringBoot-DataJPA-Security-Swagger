package com.naskoni.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Data
@MappedSuperclass
public abstract class AbstractEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Setter(AccessLevel.PRIVATE)
  @CreationTimestamp
  @Column(name = "created", columnDefinition = "timestamp(3)", nullable = false, updatable = false)
  private Instant created;

  @Setter(AccessLevel.PRIVATE)
  @UpdateTimestamp
  @Column(name = "updated", columnDefinition = "timestamp(3)", nullable = false)
  private Instant updated;
}