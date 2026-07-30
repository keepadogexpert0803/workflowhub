package com.port.myport.dto;

import com.port.myport.domain.UserRole;
import lombok.Data;

@Data
public class UserRegisterRequest {
    private String userId;
    private String passwd;
    private String userName;
    private UserRole role;
}
