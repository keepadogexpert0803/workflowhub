package com.port.myport.repository;

import com.port.myport.domain.TaskHistory;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskHistoryRepository extends JpaRepository<TaskHistory, Long> {
    List<TaskHistory> findByTask_IdOrderByCreatedAtAsc(Long taskId);

    @EntityGraph(attributePaths = {"task", "changedBy"})
    List<TaskHistory> findTop5ByOrderByCreatedAtDesc();
}
