package com.naskoni.library.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;

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
