package com.port.myport.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String userId;
    private String passwd;
}
