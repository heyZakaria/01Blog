package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.CreateUserRequest;
import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UpdateUserRequest;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.PostService;
import com.zone.zone01blog.service.SubscriptionService;
import com.zone.zone01blog.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    // /profile
    // /feed ==> just who you following + your posts ==> posts from the following

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final PostService postService;

    // Constructor injection
    public UserController(UserService userService, SubscriptionService subscriptionService, PostService postService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.postService = postService;
    }

    // choooooof any profile
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.userId")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        UserDTO user = userService.getUserById(id);

        user.setFollowersCount(subscriptionService.getFollowersCount(id));
        user.setFollowingCount(subscriptionService.getFollowingCount(id));

        if (auth != null) {
            String currentUserId = auth.getUserId();
            if (!currentUserId.equals(id)) {
                user.setIsFollowedByCurrentUser(
                        subscriptionService.isFollowing(currentUserId, id));
            }
        }
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDTO>> getUserPosts(
            @PathVariable String id,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String currentUserId = auth.getUserId();
        List<PostDTO> posts = postService.getPostsByUserId(id, currentUserId);
        return ResponseEntity.ok(posts);
    }

    // admiiiiiiin
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // check this later @PreAuthorize
    // @PreAuthorize("#id == authentication.principal.userId")
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        UserDTO user = userService.getUserById(userId);

        // Add follower counts
        user.setFollowersCount(subscriptionService.getFollowersCount(userId));
        user.setFollowingCount(subscriptionService.getFollowingCount(userId));

        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
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
    public UserDTO updateUser(@PathVariable String id, @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
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
