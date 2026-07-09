package com.port.myport.dto;

import com.port.myport.domain.TaskPriority;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkTaskCreateRequest {

    private String title;

    private String content;

    private TaskPriority priority;

    private LocalDate dueDate;

    private String createdBy;

    private String assignedTo;
}
