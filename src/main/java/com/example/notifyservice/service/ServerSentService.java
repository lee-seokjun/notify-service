package com.example.notifyservice.service;

import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.elastic.Alert;
import com.example.notifyservice.elastic.AlertRepository;
import com.example.notifyservice.vo.RequestAlert;
import lombok.extern.slf4j.Slf4j;
import org.infinispan.Cache;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class ServerSentService implements IServerSentService{
    private final AlertRepository repository;
//    private final Map<String, Sinks.Many> sinks;
    Cache<String, SseEmitter> sseEmitterCache;
    Cache<String, String> sseEventCache;

    public ServerSentService(AlertRepository repository, Cache<String, SseEmitter> sseEmitterCache, Cache<String, String> sseEventCache) {
        this.repository = repository;
        this.sseEmitterCache = sseEmitterCache;
        this.sseEventCache = sseEventCache;
    }

    @Override
    public Mono<Void> delete(String alertId) {
        return repository.findById(alertId).flatMap(repository::delete);
    }

    @Override
    public Flux<Alert> findByUserId(String userId) {
        return repository.findByToUser(userId);
    }

    @Override
    public Mono<Alert> save(AlertDto request ) {
        ModelMapper modelMapper = new ModelMapper();
        Alert alert = modelMapper.map(request,Alert.class);
        alert.setAlertId(UUID.randomUUID().toString());
        String toUser = request.getToUser();
        return repository.save(alert).doOnNext(
                    c -> {
                        sseEmitterCache.forEach(
                                (key,emitter) ->
                                {
                                    if( key.startsWith(toUser+"_"))
                                    {
                                        SseEmitter.SseEventBuilder event = emitter.event()
                                                .data(c);
                                        try {
                                            emitter.send(event);
                                        }catch(IOException | IllegalStateException e){
                                            log.error(e.getMessage());
                                            sseEmitterCache.remove(key);
                                        }
                                    }

                                }


                        );

                    }
                    );

    }

    @Override
    public Mono<SseEmitter> getSink(String userId) {
        SseEmitter  sseEmitter = new SseEmitter( 10l * 60l * 1000l);
        String eventName = userId+"_"+System.currentTimeMillis();
        sseEmitterCache.put(eventName, sseEmitter);
        return Mono.just(sseEmitter);
    }
}
