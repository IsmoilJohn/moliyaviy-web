package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.UserRequest;
import com.moliyaviy.web.dto.UserResponse;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.exception.DuplicateResourceException;
import com.moliyaviy.web.exception.ResourceNotFoundException;
import com.moliyaviy.web.repository.UserRepository;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CategorySeedService categorySeedService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(UserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new DuplicateResourceException("Email already registered: " + request.email());
        }

        User user = new User();
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setFullName(request.fullName());
        user = userRepository.save(user);

        categorySeedService.seedDefaultCategories(user);

        return UserResponse.from(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getById(UUID id) {
        return userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

}
