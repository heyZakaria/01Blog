package com.zone.zone01blog.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zone.zone01blog.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // No SQL written! Spring Data generates everything!

    // Custom query methods (Spring Data generates implementation) JPQL
    // @Query("SELECT user FROM users u WHERE u.email = :email") ❌ ❌ 3 errors find them
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);
    //OR
    // Optional<User> findByEmail(String email);
    // Spring JPA/DATA will create a JPQL ==> SELECT u FROM User u WHERE u.email = ?1
    // Another ex: findByEmailAndStatus(String email, Status status); ==> WHERE email = ?1 AND status = ?2

    // Old In-Memory Repository
    /* private List<User> users = new ArrayList<>();

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
    } */
}