package com.example.notifyservice.service;

import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.elastic.Alert;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

public interface IServerSentService {
    Mono<Void> delete(String alertId);
    Flux<Alert>findByUserId(String userId);
    Mono<Alert> save(AlertDto request);
    Mono<SseEmitter> getSink(String userId);
}
