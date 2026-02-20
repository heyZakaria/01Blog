package com.zone.zone01blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.service.PostService;
import com.zone.zone01blog.service.SubscriptionService;
import com.zone.zone01blog.service.UserService;

@RestController
@RequestMapping("/api/v1/public/users")
public class PublicUserController {

    private final UserService userService;
    private final SubscriptionService subscriptionService;
    private final PostService postService;

    public PublicUserController(UserService userService, SubscriptionService subscriptionService, PostService postService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getPublicUserById(@PathVariable String id) {
        UserDTO user = userService.getUserById(id);
        user.setFollowersCount(subscriptionService.getFollowersCount(id));
        user.setFollowingCount(subscriptionService.getFollowingCount(id));
        user.setIsFollowedByCurrentUser(false);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}/posts")
    public ResponseEntity<List<PostDTO>> getPublicUserPosts(@PathVariable String id) {
        List<PostDTO> posts = postService.getPostsByUserId(id, null);
        return ResponseEntity.ok(posts);
    }
}
