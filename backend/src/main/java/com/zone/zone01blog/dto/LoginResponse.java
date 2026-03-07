package com.zone.zone01blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class LoginResponse{
     private String token;
    private UserDTO user;
}
