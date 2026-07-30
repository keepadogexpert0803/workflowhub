package com.port.myport.repository;

import com.port.myport.domain.User;
import com.port.myport.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    List<User> findByRole(UserRole role);
}
