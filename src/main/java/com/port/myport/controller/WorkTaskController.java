package com.port.myport.controller;

import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.dto.WorkTaskResponse;
import com.port.myport.service.WorkTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class WorkTaskController {
    private final WorkTaskService workTaskService;

    @PostMapping
    public Long createTask(@RequestBody WorkTaskCreateRequest request) {
        return workTaskService.createTask(request);
    }

    @GetMapping("/{taskId}")
    public WorkTaskResponse findTask(@PathVariable Long taskId) {
        return workTaskService.findTask(taskId);
    }
}
