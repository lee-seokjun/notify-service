package com.example.notifyservice.messagequeue;


import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.service.ServerSentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {

    ServerSentService service;

    public KafkaConsumer(ServerSentService service) {
        this.service = service;
    }

    @KafkaListener(topics = "message-send")
    public void createAlert(String kafkaMessage){
        log.info("kafka Message : ->" + kafkaMessage );
        Map<Object,Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map =mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() {
            });
        }catch(JsonProcessingException x){
            x.printStackTrace();
        }
        String users = (String) map.get("toUsers") ;
        String senderId = (String) map.get("senderId") ;
        String message = (String) map.get("message") ;
        String fromUser = (String) map.get("fromUser") ;
        Arrays.stream(users.split(","))
                .filter(v->!v.equals(senderId))
                .forEach(v->
                        service.save(new AlertDto(v,senderId,message)).subscribe()
        );




    }
}
