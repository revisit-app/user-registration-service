package io.github.revisit_app.userregistrationservice.utils;

import lombok.Data;

@Data
public class NewUser {
  
  private String firstName;

  private String lastName;

  private String username;

  private String password;
}
