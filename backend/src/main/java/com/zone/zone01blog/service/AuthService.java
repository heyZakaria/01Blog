package com.zone.zone01blog.service;

import org.springframework.stereotype.Service;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.LoginRequest;
import com.zone.zone01blog.dto.LoginResponse;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.InvalidCredentialsException;
import com.zone.zone01blog.repository.UserRepository;
import com.zone.zone01blog.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, UserService userService, PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getRole());

        UserDTO userDTO = userService.convertToDTO(user); // Need to make this public!

        return new LoginResponse(token, userDTO);
    }

    public LoginResponse register(CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);

        String token = jwtUtil.generateToken(createdUser.getId(), createdUser.getRole());

        return new LoginResponse(token, createdUser);
    }
}
