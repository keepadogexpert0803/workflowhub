package com.port.myport.dto;

import com.port.myport.domain.TaskHistory;
import com.port.myport.domain.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskHistoryResponse {
    private Long id;
    private Long taskId;
    private TaskStatus beforeStatus;
    private TaskStatus afterStatus;
    private String changedBy;
    private String comment;
    private LocalDateTime createdAt;

    public static TaskHistoryResponse from(TaskHistory history) {
        TaskHistoryResponse response = new TaskHistoryResponse();
        response.setId(history.getId());
        response.setTaskId(history.getTask().getId());
        response.setBeforeStatus(history.getBeforeStatus());
        response.setAfterStatus(history.getAfterStatus());
        response.setChangedBy(history.getChangedBy().getUserId());
        response.setComment(history.getComment());
        response.setCreatedAt(history.getCreatedAt());
        return response;
    }
}
