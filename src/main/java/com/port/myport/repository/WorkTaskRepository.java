package com.port.myport.repository;

import com.port.myport.domain.TaskStatus;
import com.port.myport.domain.WorkTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {
    @Query("""
        select t
        from WorkTask t
        where (:status is null or t.status = :status)
          and (:assignedTo is null or t.assignedTo.userId = :assignedTo)
          and (:createdBy is null or t.createdBy.userId = :createdBy)
          and (:keyword is null or t.title like concat('%', :keyword, '%'))
          and (:dueDateFrom is null or t.dueDate >= :dueDateFrom)
          and (:dueDateTo is null or t.dueDate <= :dueDateTo)
        order by t.id desc
    """)
    Page<WorkTask> searchTasks(
            @Param("status") TaskStatus status,
            @Param("assignedTo") String assignedTo,
            @Param("createdBy") String createdBy,
            @Param("keyword") String keyword,
            @Param("dueDateFrom") LocalDate dueDateFrom,
            @Param("dueDateTo") LocalDate dueDateTo,
            Pageable pageable
    );
}
