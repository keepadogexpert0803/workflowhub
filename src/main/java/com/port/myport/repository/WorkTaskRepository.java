package com.port.myport.repository;

import com.port.myport.domain.WorkTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkTaskRepository extends JpaRepository<WorkTask, Long> {
}
