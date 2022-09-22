package com.example.notifyservice.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestAlert {
    private String toUser;
    private String fromUser;
    private String message;
    private String readFlag;
}
