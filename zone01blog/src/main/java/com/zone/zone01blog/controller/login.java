/* package com.zone.zone01blog.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@RestController
public class login {
    
    
    private String username;
    private String password;

    public void GetLogIn(){

    }
}



@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/")
public class LoginController {

	@Autowired
	private EmployeeRepository employeeRepository;
	
	// get all employees
	@GetMapping("/employees")
	public List<Employee> getAllEmployees(){
		return employeeRepository.findAll();
	} */