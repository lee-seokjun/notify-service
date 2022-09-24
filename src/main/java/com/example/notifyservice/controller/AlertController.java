package com.example.notifyservice.controller;

import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.elastic.Alert;
import com.example.notifyservice.service.IServerSentService;
import com.example.notifyservice.vo.RequestAlert;

import org.modelmapper.ModelMapper;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;



@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AlertController {
    private IServerSentService service;

    public AlertController(IServerSentService service) {
        this.service = service;
    }
    @GetMapping("/alert/{userId}")
    public Flux<Alert> getMyAlert(@PathVariable String userId){
        return  service.findByUserId(userId);
    }
    @GetMapping("/alert/sse/{userId}")
    public Flux<ServerSentEvent<Alert>> sse(@PathVariable String userId){

        Sinks.Many sink  = service.getSink(userId);

        return sink.asFlux().map(c-> ServerSentEvent.builder(c).build());
    }
    @PostMapping("/alert")
    public Mono<Alert> save(@RequestBody  RequestAlert request){
        return service.save(new ModelMapper().map(request, AlertDto.class));
    }
    @DeleteMapping("/alert/{alertId}")
    public Mono<Void> delete(@PathVariable String alertId){
        Mono<Void> deleteAlert = service.delete(alertId);

        return Mono.when(deleteAlert).then();
    }
//    @PostMapping("/alert/{alertId}")
//    public Mono<Alert> update(@PathVariable Long alertId){
//
//    }
}
