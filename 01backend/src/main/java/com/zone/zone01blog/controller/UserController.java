package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.UpdateUserRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.exception.UnauthorizedAccessException;
import com.zone.zone01blog.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import com.zone.zone01blog.util.AuthContext;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final AuthContext authContext;

    // Constructor injection
    public UserController(UserService userService, AuthContext authContext) {
        this.userService = userService;
        this.authContext = authContext;
    }

    // choooooof any profile
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/ana")
    public ResponseEntity<UserDTO> getUserById() {
        String userId = authContext.getCurrentUserId();
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    // admiiiiiiin
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

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

    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable String id, @RequestBody UpdateUserRequest request) {
        String userId = authContext.getCurrentUserId();

        if(!id.equals(userId)){
            throw new UnauthorizedAccessException("You can only update your own profile");
        }

        UserDTO updateUser = userService.updateUser(request, userId);
        return updateUser;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable String id) {
        String userId = authContext.getCurrentUserId();
        
        if(!id.equals(userId)){
            throw new UnauthorizedAccessException("You can only delete your own profile");
        }
        userService.deleteUser(userId);


        return ResponseEntity.noContent().build();
    }

}
