package com.port.myport.dto;

import lombok.Data;

@Data
public class TaskReviewRequest {
    private String reviewerId;
    private String comment;
}
