package com.zone.zone01blog.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zone.zone01blog.exception.ResourceNotFoundException;
import com.zone.zone01blog.model.User;
import com.zone.zone01blog.repository.UserRepository;


@CrossOrigin(origins = "http://localhost:3333")
@RestController
@RequestMapping("/api/v1/")
public class user {
    
    @Autowired
	private UserRepository userRepository;
	
	// get all users
	@GetMapping("/users")
	public List<User> getAllusers(){
		System.out.println("waaaaaaaaaaaa "  );
		return userRepository.findAll();
	}

	@PostMapping("/users")
	public User createUser(@RequestBody User u){
		System.out.println("weeeeeeeeeeeeeeeeee " + u );
		return userRepository.save(u);
	}

	@GetMapping("/users/{id}")
	public ResponseEntity<User> getUserById(@PathVariable long id) {
		
			User u = userRepository.findById(id).orElseThrow(() -> 
			new  ResourceNotFoundException("USER NOT FOUND ID =" + id)); 
			
			return ResponseEntity.ok(u);

	}



	@PutMapping("/users/{id}")
	public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody User newUserData) {
		
			User user = userRepository.findById(id).orElseThrow(() -> 
			new  ResourceNotFoundException("user not found id =" + id)); 
			

			user.setEmail(newUserData.getEmail());
			user.setUserName(newUserData.getUserName());
			
			User updatedUser = userRepository.save(user);

			return ResponseEntity.ok(updatedUser);

	}



	@DeleteMapping("/users/{id}")
	public ResponseEntity<HashMap<String, Boolean>> updateUser(@PathVariable long id) {
		
			User user = userRepository.findById(id).orElseThrow(() -> 
			new  ResourceNotFoundException("user not found id =" + id)); 
			

			userRepository.delete(user);
			
			HashMap<String, Boolean> response = new HashMap<>();
			response.put("deleted", true);

			return ResponseEntity.ok(response);

	}
	
}


	