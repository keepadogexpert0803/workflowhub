package com.port.myport.service;

import com.port.myport.domain.TaskHistory;
import com.port.myport.domain.TaskStatus;
import com.port.myport.domain.User;
import com.port.myport.domain.WorkTask;
import com.port.myport.dto.*;
import com.port.myport.repository.TaskHistoryRepository;
import com.port.myport.repository.UserRepository;
import com.port.myport.repository.WorkTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkTaskService {
    private final WorkTaskRepository workTaskRepository;
    private final TaskHistoryRepository taskHistoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createTask(WorkTaskCreateRequest request) {
        User creator = userRepository.findById(request.getCreatedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getCreatedBy()));

        User assignee = null;
        if (request.getAssignedTo() != null) {
            assignee = userRepository.findById(request.getAssignedTo())
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getAssignedTo()));
        }

        LocalDateTime now = LocalDateTime.now();

        WorkTask task = new WorkTask();
        task.setTitle(request.getTitle());
        task.setContent(request.getContent());
        task.setPriority(request.getPriority());
        task.setStatus(TaskStatus.CREATED);
        task.setDueDate(request.getDueDate());
        task.setCreatedBy(creator);
        task.setAssignedTo(assignee);
        task.setCreatedAt(now);

        WorkTask savedTask = workTaskRepository.save(task);

        TaskHistory history = new TaskHistory();
        history.setTask(savedTask);
        history.setBeforeStatus(null);
        history.setAfterStatus(TaskStatus.CREATED);
        history.setChangedBy(creator);
        history.setComment("Task created");
        history.setCreatedAt(now);

        taskHistoryRepository.save(history);

        return savedTask.getId();
    }

    @Transactional(readOnly = true)
    public WorkTaskResponse findTask(Long taskId) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        return WorkTaskResponse.from(task);
    }

    @Transactional(readOnly = true)
    public List<WorkTaskResponse> findTasks() {
        return workTaskRepository.findAll()
                .stream()
                .map(WorkTaskResponse::from)
                .toList();
    }

    @Transactional
    public void assignTask(Long taskId, TaskAssignRequest request) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getAssigneeId()));

        User manager = userRepository.findById(request.getManagerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getManagerId()));
        if (task.getStatus() != TaskStatus.CREATED) {
            throw new IllegalArgumentException("Only CREATED tasks can be assigned.");
        }

        TaskStatus beforeStatus = task.getStatus();

        task.setAssignedTo(assignee);
        task.setStatus(TaskStatus.ASSIGNED);
        LocalDateTime now = LocalDateTime.now();
        task.setUpdatedAt(now);

        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setBeforeStatus(beforeStatus);
        history.setAfterStatus(TaskStatus.ASSIGNED);
        history.setChangedBy(manager);
        history.setComment(request.getComment());
        history.setCreatedAt(now);

        taskHistoryRepository.save(history);
    }

    @Transactional
    public void changeStatus(Long taskId, TaskStatusChangeRequest request) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        User changedBy = userRepository.findById(request.getChangedBy())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getChangedBy()));

        TaskStatus beforeStatus = task.getStatus();

        TaskStatus status = request.getStatus();
        validateStatus(beforeStatus, status);
        task.setStatus(status);
        LocalDateTime now = LocalDateTime.now();
        task.setUpdatedAt(now);

        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setBeforeStatus(beforeStatus);
        history.setAfterStatus(status);
        history.setChangedBy(changedBy);
        history.setComment(request.getComment());
        history.setCreatedAt(now);

        taskHistoryRepository.save(history);
    }

    @Transactional
    public void approveTask(Long taskId, TaskReviewRequest request) {
        reviewTask(taskId, request, TaskStatus.APPROVED);
    }

    @Transactional
    public void rejectTask(Long taskId, TaskReviewRequest request) {
        reviewTask(taskId, request, TaskStatus.REJECTED);
    }

    private void reviewTask(Long taskId, TaskReviewRequest request, TaskStatus nextStatus) {
        WorkTask task = workTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        User reviewer = userRepository.findById(request.getReviewerId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getReviewerId()));
        if (task.getStatus() != TaskStatus.SUBMITTED) {
            throw new IllegalArgumentException("Only SUBMITTED tasks can be reviewed.");
        }

        TaskStatus beforeStatus = task.getStatus();

        TaskStatus status = nextStatus;
        task.setStatus(status);
        LocalDateTime now = LocalDateTime.now();
        task.setUpdatedAt(now);

        TaskHistory history = new TaskHistory();
        history.setTask(task);
        history.setBeforeStatus(beforeStatus);
        history.setComment(request.getComment());
        history.setChangedBy(reviewer);
        history.setAfterStatus(status);
        history.setCreatedAt(now);

        taskHistoryRepository.save(history);
    }

    private void validateStatus(TaskStatus currentStatus, TaskStatus nextStatus) {
        boolean valid =
                (currentStatus == TaskStatus.ASSIGNED && nextStatus == TaskStatus.IN_PROGRESS)
                        || (currentStatus == TaskStatus.IN_PROGRESS && nextStatus == TaskStatus.SUBMITTED);

        if (!valid) {
            throw new IllegalArgumentException("Invalid status transition: " + currentStatus + " -> " + nextStatus);
        }
    }

    @Transactional(readOnly = true)
    public List<TaskHistoryResponse> findTaskHistories(Long taskId) {
        return taskHistoryRepository.findByTask_IdOrderByCreatedAtAsc(taskId)
                .stream()
                .map(TaskHistoryResponse::from)
                .toList();
    }

}
