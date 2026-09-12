package com.naskoni.library.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "client")
public class Client extends AbstractEntity {

  @Column(nullable = false)
  private String name;

  private String pid;

  private LocalDate birthdate;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;
}
