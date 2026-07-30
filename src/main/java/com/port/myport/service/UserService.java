package com.port.myport.service;

import com.port.myport.domain.User;
import com.port.myport.domain.UserRole;
import com.port.myport.dto.LoginRequest;
import com.port.myport.dto.UserRegisterRequest;
import com.port.myport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void register(UserRegisterRequest request) {
        if (userRepository.existsById(request.getUserId())) {
            throw new IllegalArgumentException("User already exists: " + request.getUserId());
        }

        User user = new User();
        user.setUserId(request.getUserId());
        user.setPasswd(request.getPasswd());
        user.setUserName(request.getUserName());
        user.setRole(request.getRole() == null ? UserRole.USER : request.getRole());

        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(LoginRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));

        if (!user.getPasswd().equals(request.getPasswd())) {
            throw new IllegalArgumentException("Invalid password.");
        }

        return user;
    }

    @Transactional(readOnly = true)
    public List<User> findUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }
}
