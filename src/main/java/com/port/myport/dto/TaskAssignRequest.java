package com.port.myport.dto;

import lombok.Data;

@Data
public class TaskAssignRequest {
    private String assigneeId;
    private String managerId;
    private String comment;
}
