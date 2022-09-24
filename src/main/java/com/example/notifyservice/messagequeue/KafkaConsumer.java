package com.example.notifyservice.messagequeue;


import com.example.notifyservice.dto.AlertDto;
import com.example.notifyservice.service.ServerSentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class KafkaConsumer {

    ServerSentService service;

    public KafkaConsumer(ServerSentService service) {
        this.service = service;
    }

    @KafkaListener(topics = "sse-alert-topic")
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
        AlertDto alertDto =new AlertDto();
        alertDto.setMessage((String) map.get("message"));
        alertDto.setFromUser((String) map.get("fromUser"));
        alertDto.setToUser((String) map.get("toUser"));
         service.save(alertDto).subscribe();


    }
}
