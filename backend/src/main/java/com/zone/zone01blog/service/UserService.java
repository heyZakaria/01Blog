package com.zone.zone01blog.service;

import org.springframework.stereotype.Service;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.UpdateUserRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.exception.UserNotFoundException;
import com.zone.zone01blog.repository.UserRepository;

import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
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

    // User entity for internal use
    public User getUserEntityById(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    public User getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert User -> UserDTO (for outside responce no password)
    public UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public UserDTO createUser(CreateUserRequest request) {
        String id = UUID.randomUUID().toString();
        String hashedPassword = this.passwordEncoder.encode(request.getPassword());

        if (request.getRole() == null) {
            request.setRole("user");
        }
        User user = new User(
                id,
                request.getName(),
                request.getEmail().toLowerCase(),
                hashedPassword,
                request.getRole());

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(UpdateUserRequest request, String id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        existingUser.setName(request.getName());
        existingUser.setEmail(request.getEmail().toLowerCase());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(hashed);
        }

        existingUser.setRole(request.getRole());

        User updated = userRepository.save(existingUser);

        return convertToDTO(updated);
    }

    public void deleteUser(String id) {
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        userRepository.deleteById(id);
    }

    public UserDTO toggleBan(String userId) {
        User user = getUserEntityById(userId);
        user.setBanned(!user.isBanned());
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }
}
