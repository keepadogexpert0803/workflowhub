package com.port.myport.dto;

import com.port.myport.domain.TaskStatus;
import lombok.Data;

@Data
public class TaskStatusChangeRequest {
    private TaskStatus status;
    private String changedBy;
    private String comment;
}
