package com.zone.zone01blog.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateUserRequest {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @Email(message = "Invalid email format")
    private String email;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    private String role;

    
}
