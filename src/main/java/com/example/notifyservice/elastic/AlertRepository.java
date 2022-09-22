package com.example.notifyservice.elastic;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface AlertRepository extends ReactiveCrudRepository<Alert,String> {


    Flux<Alert> findByToUser(String toUser);
}
