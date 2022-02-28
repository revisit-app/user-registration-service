package io.github.revisit_app.userregistrationservice.utils;

import lombok.Data;

@Data
public class UserData {
  
  private final long id;

  private final String firstName;

  private final String lastName;

  private final String username;
}
