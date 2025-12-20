package com.zone.zone01blog.service;

import org.springframework.stereotype.Service;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.UpdateUserRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.UserNotFoundException;
import com.zone.zone01blog.repository.UserRepository;
import com.zone.zone01blog.util.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return convertToDTO(user);
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert User -> UserDTO (removes password!)
    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole());
    }

    public UserDTO createUser(CreateUserRequest request) {
        String id = UUID.randomUUID().toString();
        String hashedPassword = this.passwordEncoder.encode(request.getPassword());
        System.out.println("hashedPassword" + " " + hashedPassword);
        LocalDateTime timestamp = LocalDateTime.now();
        User user = new User(
                id,
                request.getName(),
                request.getEmail(),
                hashedPassword,
                request.getRole(),
                timestamp);

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(UpdateUserRequest request, String id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(hashed);
        }

        existingUser.setRole(request.getRole());

        User updated = userRepository.update(existingUser)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        return convertToDTO(updated);
    }

    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        // i need to make it like this
        // .orElseThrow(() -> new UserNotFoundException("User not found with id: " +
        // id));

        userRepository.deleteUser(id);
    }
}
