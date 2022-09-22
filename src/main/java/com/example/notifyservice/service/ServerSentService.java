package com.example.notifyservice.service;

import com.example.notifyservice.config.CacheConfig;
import com.example.notifyservice.elastic.Alert;
import com.example.notifyservice.elastic.AlertRepository;
import com.example.notifyservice.vo.RequestAlert;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ServerSentService implements IServerSentService{
    private final AlertRepository repository;
    private final Map<String, Sinks.Many> sinks;
    public ServerSentService(AlertRepository repository) {
        this.repository = repository;
        sinks= new HashMap<>();
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
    public Mono<Alert> save(RequestAlert request ) {
        ModelMapper modelMapper = new ModelMapper();
        Alert alert = modelMapper.map(request,Alert.class);
        alert.setAlertId(UUID.randomUUID().toString());
        String toUser = request.getToUser();
        if(   sinks.get(toUser) ==null)
        {
            return   repository.save(alert);
        }
        else
        {
            return repository.save(alert).doOnNext(c -> sinks.get(request.getToUser()).tryEmitNext(c));
        }
    }

    @Override
    public Sinks.Many getSink(String userId) {
        Sinks.Many sink;
        if(   sinks.get(userId) ==null) {
            sink = Sinks.many().multicast().onBackpressureBuffer();
            sinks.put(userId,sink);
        }else {
             sink = sinks.get(userId);
        }
        return sink;
    }
}
