package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.UpdateUserRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.exception.UnauthorizedAccessException;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    // Constructor injection
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // choooooof any profile
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // admiiiiiiin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }


    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(
        @AuthenticationPrincipal JwtAuthenticationToken auth
    ) {
        String userId = auth.getUserId();
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody CreateUserRequest request) {
        UserDTO createdUser = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    // OR dir hadi
    // @ResponseStatus(HttpStatus.CREATED)
    // @PostMapping
    // public UserDTO createUser(@RequestBody CreateUserRequest request) {
    // return userService.createUser(request);
    // }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request, @AuthenticationPrincipal JwtAuthenticationToken auth) {
        UserDTO updateUser = userService.updateUser(request, id);
        return updateUser;
    }

    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable String id) {
    
        userService.deleteUser(id);

        return ResponseEntity.noContent().build();
    }

}
