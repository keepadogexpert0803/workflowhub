package com.port.myport.dto;

import com.port.myport.domain.TaskPriority;
import com.port.myport.domain.TaskStatus;
import com.port.myport.domain.WorkTask;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class WorkTaskResponse {
    private Long id;
    private String title;
    private String content;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
    private String createdBy;
    private String assignedTo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WorkTaskResponse from(WorkTask task) {
        WorkTaskResponse response = new WorkTaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setContent(task.getContent());
        response.setStatus(task.getStatus());
        response.setPriority(task.getPriority());
        response.setDueDate(task.getDueDate());
        response.setCreatedBy(task.getCreatedBy().getUserId());
        response.setAssignedTo(task.getAssignedTo() == null ? null : task.getAssignedTo().getUserId());
        response.setCreatedAt(task.getCreatedAt());
        response.setUpdatedAt(task.getUpdatedAt());
        return response;
    }
}
