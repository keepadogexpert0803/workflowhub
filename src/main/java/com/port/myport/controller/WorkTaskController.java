package com.port.myport.controller;

import com.port.myport.dto.TaskAssignRequest;
import com.port.myport.dto.TaskHistoryResponse;
import com.port.myport.dto.TaskReviewRequest;
import com.port.myport.dto.TaskStatusChangeRequest;
import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.dto.WorkTaskResponse;
import com.port.myport.dto.WorkTaskSearchCondition;
import com.port.myport.service.WorkTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @PatchMapping("/{taskId}/assign")
    public void assignTask(@PathVariable Long taskId,
                           @RequestBody TaskAssignRequest request) {
        workTaskService.assignTask(taskId, request);
    }

    @PatchMapping("/{taskId}/status")
    public void changeStatus(@PathVariable Long taskId,
                             @RequestBody TaskStatusChangeRequest request) {
        workTaskService.changeStatus(taskId, request);
    }

    @PatchMapping("/{taskId}/approve")
    public void approveTask(@PathVariable Long taskId,
                            @RequestBody TaskReviewRequest request) {
        workTaskService.approveTask(taskId, request);
    }

    @PatchMapping("/{taskId}/reject")
    public void rejectTask(@PathVariable Long taskId,
                           @RequestBody TaskReviewRequest request) {
        workTaskService.rejectTask(taskId, request);
    }
    @GetMapping("/{taskId}/histories")
    public List<TaskHistoryResponse> historyTask(@PathVariable Long taskId){
        return workTaskService.findTaskHistories(taskId);
    }
    @GetMapping
    public Page<WorkTaskResponse> findTasks(WorkTaskSearchCondition condition, Pageable pageable) {
        return workTaskService.findTasks(condition, pageable);
    }

}
