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

    public List<UserDTO> getDiscoverUsers(String userId) {
        return userRepository.findUsersNotFollowed(userId)
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
                user.isBanned(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

    public UserDTO createUser(CreateUserRequest request) {
        String id = UUID.randomUUID().toString();
        String hashedPassword = this.passwordEncoder.encode(request.getPassword());

        String role = normalizeRole(request.getRole());
        if (role == null) {
            role = "USER";
        }
        String normalizedEmail = request.getEmail().toLowerCase();
        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalStateException("Email already exists");
        }
        User user = User.builder()
                .id(id)
                .name(request.getName())
                .email(normalizedEmail)
                .password(hashedPassword)
                .role(role)
                .build();

        User savedUser = userRepository.save(user);

        return convertToDTO(savedUser);
    }

    public UserDTO updateUser(UpdateUserRequest request, String id) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if (request.getName() != null && !request.getName().isBlank()) {
            existingUser.setName(request.getName());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String normalizedEmail = request.getEmail().toLowerCase();
            Optional<User> existingByEmail = userRepository.findByEmail(normalizedEmail);
            if (existingByEmail.isPresent() && !existingByEmail.get().getId().equals(existingUser.getId())) {
                throw new IllegalStateException("Email already exists");
            }
            existingUser.setEmail(normalizedEmail);
        }

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            String hashed = passwordEncoder.encode(request.getPassword());
            existingUser.setPassword(hashed);
        }

        if (request.getRole() != null && !request.getRole().isBlank()) {
            existingUser.setRole(normalizeRole(request.getRole()));
        }

        User updated = userRepository.save(existingUser);

        return convertToDTO(updated);
    }

    public void deleteUser(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {
            long adminCount = userRepository.countByRole("ADMIN");
            if (adminCount <= 1) {
                throw new IllegalStateException("Cannot delete the last admin account");
            }
        }

        userRepository.deleteById(id);
    }

    public UserDTO toggleBan(String userId) {
        User user = getUserEntityById(userId);
        user.setBanned(!user.isBanned());
        // Force logout by invalidating existing tokens
        user.setTokenVersion(user.getTokenVersionSafe() + 1);
        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return null;
        }
        String trimmed = role.trim();
        return trimmed.isEmpty() ? null : trimmed.toUpperCase();
    }
}
