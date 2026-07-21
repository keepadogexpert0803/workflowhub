package com.port.myport.dto;

import com.port.myport.domain.TaskStatus;
import lombok.Data;

import java.time.LocalDate;

@Data
public class WorkTaskSearchCondition {

    private TaskStatus status;

    private String assignedTo;

    private String createdBy;

    private LocalDate dueDateFrom;

    private LocalDate dueDateTo;

    private String keyword;
}
