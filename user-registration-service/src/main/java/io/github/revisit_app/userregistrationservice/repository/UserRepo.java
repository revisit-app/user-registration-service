package io.github.revisit_app.userregistrationservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import io.github.revisit_app.userregistrationservice.entity.User;
import reactor.core.publisher.Mono;

public interface UserRepo extends ReactiveCrudRepository<User, Long>{
  
  Mono<User> findByUsername(String username);
}
