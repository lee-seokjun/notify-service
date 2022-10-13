package com.example.notifyservice.controller;

import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.elastic.Alert;
import com.example.notifyservice.service.IServerSentService;
import com.example.notifyservice.vo.RequestAlert;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;




@RestController
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
@Slf4j
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
    public Mono<SseEmitter> sse(@PathVariable String userId){

        return service.getSink(userId);
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
