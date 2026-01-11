package com.zone.zone01blog.controller;

import com.zone.zone01blog.dto.UserDTO;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users/api/v1")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<Map<String, Object>> toggleFollow(
            @PathVariable String userId,
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String currentUserId = auth.getUserId();
        boolean following = subscriptionService.toggleFollow(userId, currentUserId);
        long followersCount = subscriptionService.getFollowersCount(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("following", following);
        response.put("followersCount", followersCount);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me/following")
    public ResponseEntity<List<UserDTO>> getMyFollowing(
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        List<UserDTO> following = subscriptionService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/me/followers")
    public ResponseEntity<List<UserDTO>> getMyFollowers(
            @AuthenticationPrincipal JwtAuthenticationToken auth) {
        String userId = auth.getUserId();
        List<UserDTO> followers = subscriptionService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<UserDTO>> getUserFollowing(@PathVariable String userId) {
        List<UserDTO> following = subscriptionService.getFollowing(userId);
        return ResponseEntity.ok(following);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<UserDTO>> getUserFollowers(@PathVariable String userId) {
        List<UserDTO> followers = subscriptionService.getFollowers(userId);
        return ResponseEntity.ok(followers);
    }
}