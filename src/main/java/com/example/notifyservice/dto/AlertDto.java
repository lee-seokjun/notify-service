package com.example.notifyservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlertDto {
    private String toUser;
    private String fromUser;
    private String message;
    private boolean readFlag;
}
