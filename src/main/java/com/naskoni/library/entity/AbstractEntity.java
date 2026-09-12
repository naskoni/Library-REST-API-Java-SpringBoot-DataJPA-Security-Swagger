package com.naskoni.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;
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
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created", columnDefinition = "timestamp(3)", nullable = false, updatable = false)
  private Date created;

  @Setter(AccessLevel.PRIVATE)
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "updated", columnDefinition = "timestamp(3)", nullable = false)
  private Date updated;
}
