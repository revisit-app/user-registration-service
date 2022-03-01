package io.github.revisit_app.userregistrationservice.handler;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import io.github.revisit_app.userregistrationservice.entity.User;
import io.github.revisit_app.userregistrationservice.repository.UserRepo;
import io.github.revisit_app.userregistrationservice.utils.NewUser;
import io.github.revisit_app.userregistrationservice.utils.UserData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserHandler {

  private final UserRepo ur;
  private final PasswordEncoder pe;
  private final WebClient.Builder wcb;
  private final String userProfileServiceUrl = "localhost:8085";

  private Mono<Boolean> doesUsernameExist(String username) {
    return ur.findByUsername(username)
        .flatMap(eu -> Mono.just(true))
        .switchIfEmpty(Mono.just(false));
  }

  public Mono<ServerResponse> handleGetUser(ServerRequest request) {
    var username = request.pathVariable("username");

    var response = ur.findByUsername(username)
        .flatMap(su -> {
          log.info("Getting user: {}", su.getUsername());

          var data = new UserData(su.getId(),
              su.getFirstName(),
              su.getLastName(),
              su.getUsername());

          return ServerResponse.ok().bodyValue(data);
        })
        .switchIfEmpty(ServerResponse.notFound().build());

    return response;
  }

  public Mono<ServerResponse> handleCreateUser(ServerRequest request) {
    var response = request.bodyToMono(NewUser.class)
        .flatMap(nu -> {
          return this.doesUsernameExist(nu.getUsername())
              .flatMap(doesExist -> {
                if (doesExist == true)
                  return ServerResponse.status(HttpStatus.CONFLICT).build();
                else {
                  var user = new User();
                  user.setFirstName(nu.getFirstName());
                  user.setLastName(nu.getLastName());
                  user.setUsername(nu.getUsername());
                  user.setPassword(pe.encode(nu.getPassword()));

                  return ur.save(user)
                      .flatMap(su -> {
                        log.info("User: {} created", su.getUsername());
                        
                        //Create user profile
                        wcb.build()
                            .post()
                            .uri(userProfileServiceUrl)
                            .header("userId", String.valueOf(su.getId()))
                            .retrieve()
                            .toBodilessEntity()
                            .subscribe(res -> {
                              if (res.getStatusCode() == HttpStatus.CREATED) {
                                log.info("User profile created for user: {}", su.getId());
                              }
                            });

                        return ServerResponse.created(URI.create(request.uri() + su.getUsername())).build();
                      });
                }
              });
        });

    return response;
  }
}
