package com.zone.zone01blog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.zone.zone01blog.model.User;


public interface UserRepository extends JpaRepository<User, Long>{

}

