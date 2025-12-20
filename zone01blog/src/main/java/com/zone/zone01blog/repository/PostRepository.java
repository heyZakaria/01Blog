package com.zone.zone01blog.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import com.zone.zone01blog.service.UserService;
import org.springframework.stereotype.Repository;

import com.zone.zone01blog.dto.PostDTO;
import com.zone.zone01blog.entity.Post;
import com.zone.zone01blog.entity.User;

@Repository
public class PostRepository {

    private final UserService userService;

    private List<Post> posts = new ArrayList<>();

    PostRepository(UserService userService) {
        this.userService = userService;
    }

    public Post save(Post post) {
        posts.add(post);
        return post;
    }

    public Optional<Post> findById(String id) {
        return posts.stream()
                .filter(post -> post.getId().equals(id))
                .findFirst();
    }

    public List<Post> findAll() {
        return new ArrayList<>(posts);
    }

    public List<Post> findByUserId(String userId) {
        return posts.stream()
                .filter(post -> post.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public Optional<Post> update(Post post) {
        for (int i = 0; i < posts.size(); i++) {
            if (posts.get(i).getId().equals(post.getId())) {
                posts.set(i, post);
                return Optional.of(post);
            }
        }
        return Optional.empty();
    }

    public void delete(String id) {
        posts.removeIf(post -> post.getId().equals(id));
    }

}