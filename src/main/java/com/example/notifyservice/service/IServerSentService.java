package com.example.notifyservice.service;

import com.example.notifyservice.elastic.Alert;
import com.example.notifyservice.vo.RequestAlert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public interface IServerSentService {
    Mono<Void> delete(String alertId);
    Flux<Alert>findByUserId(String userId);
    Mono<Alert> save(RequestAlert request);
    Sinks.Many getSink(String userId);
}
