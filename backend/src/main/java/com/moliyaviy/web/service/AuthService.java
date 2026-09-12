package com.moliyaviy.web.service;

import com.moliyaviy.web.dto.LoginRequest;
import com.moliyaviy.web.dto.LoginResponse;
import com.moliyaviy.web.dto.UserResponse;
import com.moliyaviy.web.entity.User;
import com.moliyaviy.web.repository.UserRepository;
import com.moliyaviy.web.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String INVALID_CREDENTIALS_MESSAGE = "Invalid email or password";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException(INVALID_CREDENTIALS_MESSAGE);
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        return new LoginResponse(token, "Bearer", UserResponse.from(user));
    }

}
