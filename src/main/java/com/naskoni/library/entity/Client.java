package com.naskoni.library.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.sql.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "client")
public class Client extends AbstractEntity {

  @Column(nullable = false)
  private String name;

  private String pid;

  private Date birthdate;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private User createdBy;
}
