package com.zone.zone01blog.repository;

import java.time.LocalDateTime;
import java.util.*;

import org.springframework.stereotype.Repository;

import com.zone.zone01blog.entity.User;

@Repository
public class UserRepository {
    private List<User> users = new ArrayList<>();

     LocalDateTime fakeTime = LocalDateTime.of(2025, 12, 25, 10, 30, 0);
    // Initialize with fake data
    public UserRepository() {
        users.add(new User("1", "Ahmed", "ahmed@example.com", "$2a$10$hashedpass1", "USER", fakeTime));
        users.add(new User("2", "Sara", "sara@example.com", "$2a$10$hashedpass2", "ADMIN", fakeTime));
        users.add(new User("3", "Youssef", "youssef@example.com", "$2a$10$hashedpass3", "USER", fakeTime));
    }

    public Optional<User> findById(String id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users); // Return copy jdida
    }

    public User save(User user) {
        users.add(user);
        return user;
    }

    public Optional<User> update(User user) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(user.getId())) {
                users.set(i, user);
                return Optional.of(user);
            }
        }

        return Optional.empty();
    }

    public void deleteUser(String id) {
        users.removeIf(user -> user.getId().equals(id));
        // users.stream()
        // .filter(user -> user.getId().equals(id)).collect(Collectors.toList());
    }
}