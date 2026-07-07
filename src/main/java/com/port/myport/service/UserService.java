package com.port.myport.service;

import com.port.myport.domain.User;
import com.port.myport.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository ;

        public void save(User user) {
            userRepository.save(user);
        }

}
