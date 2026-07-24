package com.port.myport.service;

import com.port.myport.domain.TaskPriority;
import com.port.myport.domain.TaskStatus;
import com.port.myport.domain.User;
import com.port.myport.domain.UserRole;
import com.port.myport.domain.WorkTask;
import com.port.myport.dto.TaskAssignRequest;
import com.port.myport.dto.TaskStatusChangeRequest;
import com.port.myport.dto.WorkTaskCreateRequest;
import com.port.myport.repository.TaskHistoryRepository;
import com.port.myport.repository.UserRepository;
import com.port.myport.repository.WorkTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class WorkTaskServiceTest {

    @Autowired
    WorkTaskService workTaskService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WorkTaskRepository workTaskRepository;

    @Autowired
    TaskHistoryRepository taskHistoryRepository;

    @BeforeEach
    void setUp() {
        User manager = new User();
        manager.setUserId("test_manager");
        manager.setPasswd("qwer1234");
        manager.setUserName("Test Manager");
        manager.setRole(UserRole.MANAGER);

        User user = new User();
        user.setUserId("test_user");
        user.setPasswd("1234");
        user.setUserName("Test User");
        user.setRole(UserRole.USER);

        userRepository.save(manager);
        userRepository.save(user);
    }

    @Test
    void manager_can_create_task() {
        WorkTaskCreateRequest request = new WorkTaskCreateRequest();
        request.setTitle("test create");
        request.setContent("test1234");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.of(2026, 7, 31));
        request.setCreatedBy("test_manager");
        request.setAssignedTo("test_user");

        Long taskId = workTaskService.createTask(request);
        WorkTask task = workTaskRepository.findById(taskId).orElseThrow();

        assertThat(task.getTitle()).isEqualTo("test create");
        assertThat(task.getStatus()).isEqualTo(TaskStatus.CREATED);
        assertThat(task.getCreatedBy().getUserId()).isEqualTo("test_manager");
        assertThat(task.getAssignedTo().getUserId()).isEqualTo("test_user");
        assertThat(taskHistoryRepository.findByTask_IdOrderByCreatedAtAsc(taskId)).hasSize(1);
    }

    @Test
    void user_cannot_create_task() {
        WorkTaskCreateRequest request = new WorkTaskCreateRequest();
        request.setTitle("Test task");
        request.setContent("Create task test");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.of(2026, 7, 31));
        request.setCreatedBy("test_user");
        request.setAssignedTo("test_user");

        assertThatThrownBy(() -> workTaskService.createTask(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Only ADMIN or MANAGER can perform this action.");
    }

    @Test
    void status_change_saves_history() {
        WorkTaskCreateRequest createRequest = new WorkTaskCreateRequest();
        createRequest.setTitle("Status test");
        createRequest.setContent("Status change test");
        createRequest.setPriority(TaskPriority.HIGH);
        createRequest.setDueDate(LocalDate.of(2026, 7, 31));
        createRequest.setCreatedBy("test_manager");
        createRequest.setAssignedTo("test_user");

        Long taskId = workTaskService.createTask(createRequest);

        TaskAssignRequest assignRequest = new TaskAssignRequest();
        assignRequest.setAssigneeId("test_user");
        assignRequest.setManagerId("test_manager");
        assignRequest.setComment("assign ok");

        workTaskService.assignTask(taskId, assignRequest);

        TaskStatusChangeRequest request = new TaskStatusChangeRequest();
        request.setStatus(TaskStatus.IN_PROGRESS);
        request.setChangedBy("test_user");
        request.setComment("change");

        workTaskService.changeStatus(taskId, request);

        WorkTask task = workTaskRepository.findById(taskId).orElseThrow();

        assertThat(task.getStatus()).isEqualTo(TaskStatus.IN_PROGRESS);
        assertThat(taskHistoryRepository.findByTask_IdOrderByCreatedAtAsc(taskId)).hasSize(3);
    }
}
