package com.example.notifyservice.elastic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@Document(indexName = "alert")
@AllArgsConstructor
@NoArgsConstructor
public class Alert {
    @Id
    private String alertId;
    private String toUser;
    private String fromUser;
    private String message;
    private boolean readFlag;
}
