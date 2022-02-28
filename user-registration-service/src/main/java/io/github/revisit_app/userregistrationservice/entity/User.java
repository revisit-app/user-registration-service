package io.github.revisit_app.userregistrationservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table
public class User {
  
  @Id
  private long id;

  @Column("first_name")
  private String firstName;

  @Column("last_name")
  private String lastName;

  private String username;

  private String password;
}
