package com.port.myport.controller;

import com.port.myport.dto.TaskAssignRequest;
import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.dto.WorkTaskResponse;
import com.port.myport.service.WorkTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<WorkTaskResponse> findTasks() {
        return workTaskService.findTasks();
    }

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody TaskAssignRequest request) {
        workTaskService.assignTask(taskId, request);
    }
}
