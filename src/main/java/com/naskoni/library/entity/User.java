package com.naskoni.library.entity;

import com.naskoni.library.enumeration.Role;
import com.naskoni.library.enumeration.Status;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "library_user")
public class User extends AbstractEntity {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true, length = 50)
  private String username;

  @Column(nullable = false)
  private String password;

  @Enumerated(EnumType.STRING)
  private Status status;

  @Enumerated(EnumType.STRING)
  private Role role;
}
