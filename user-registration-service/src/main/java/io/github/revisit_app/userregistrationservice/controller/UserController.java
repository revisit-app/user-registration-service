package io.github.revisit_app.userregistrationservice.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.revisit_app.userregistrationservice.handler.UserHandler;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class UserController {

  private final UserHandler uh;

  @Bean
  RouterFunction<ServerResponse> routerFunction() {

    return RouterFunctions.route()
        .POST(uh::handleCreateUser)
        .GET("/{username}", uh::handleGetUser)
        .build();
  }
}
