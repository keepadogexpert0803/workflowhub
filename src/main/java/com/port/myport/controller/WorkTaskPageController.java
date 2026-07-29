package com.port.myport.controller;

import com.port.myport.dto.TaskAssignRequest;
import com.port.myport.dto.TaskReviewRequest;
import com.port.myport.dto.TaskStatusChangeRequest;
import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.dto.WorkTaskSearchCondition;
import com.port.myport.service.WorkTaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/tasks")
public class WorkTaskPageController {
    private final WorkTaskService workTaskService;

    @GetMapping("/admin")
    public String admin(Model model) {
        return "task/admin";
    }

    @GetMapping("/page")
    public String list(WorkTaskSearchCondition condition, Pageable pageable, Model model) {
        model.addAttribute("tasks", workTaskService.findTasks(condition, pageable));
        model.addAttribute("condition", condition);
        return "task/list";
    }

    @GetMapping("/page/{taskId}")
    public String detail(@PathVariable Long taskId, Model model) {
        model.addAttribute("task", workTaskService.findTask(taskId));
        model.addAttribute("histories", workTaskService.findTaskHistories(taskId));
        return "task/detail";
    }

    @GetMapping("/page/new")
    public String createForm(Model model) {
        model.addAttribute("request", new WorkTaskCreateRequest());
        return "task/new";
    }

    @PostMapping("/page")
    public String createTask(WorkTaskCreateRequest request) {
        workTaskService.createTask(request);
        return "redirect:/tasks/page";
    }

    @PostMapping("/page/{taskId}/status")
    public String changeStatus(@PathVariable Long taskId, TaskStatusChangeRequest request) {
        workTaskService.changeStatus(taskId, request);
        return "redirect:/tasks/page/" + taskId;
    }

    @PostMapping("/page/{taskId}/assign")
    public String assignTask(@PathVariable Long taskId, TaskAssignRequest request) {
        workTaskService.assignTask(taskId, request);
        return "redirect:/tasks/page/" + taskId;
    }

    @PostMapping("/page/{taskId}/approve")
    public String approveTask(@PathVariable Long taskId, TaskReviewRequest request) {
        workTaskService.approveTask(taskId, request);
        return "redirect:/tasks/page/" + taskId;
    }

    @PostMapping("/page/{taskId}/reject")
    public String rejectTask(@PathVariable Long taskId, TaskReviewRequest request) {
        workTaskService.rejectTask(taskId, request);
        return "redirect:/tasks/page/" + taskId;
    }
}
