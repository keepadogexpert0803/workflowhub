package com.port.myport.service;

import com.port.myport.domain.TaskHistory;
import com.port.myport.domain.TaskStatus;
import com.port.myport.domain.User;
import com.port.myport.domain.WorkTask;
import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.dto.WorkTaskResponse;
import com.port.myport.repository.TaskHistoryRepository;
import com.port.myport.repository.UserRepository;
import com.port.myport.repository.WorkTaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

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
}
