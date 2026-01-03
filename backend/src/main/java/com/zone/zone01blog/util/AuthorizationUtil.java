package com.zone.zone01blog.util;

import com.zone.zone01blog.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationUtil {
    
    /**
     * Check if the authenticated user is an admin
     */
    public void requireAdmin(HttpServletRequest request) {
        String role = (String) request.getAttribute("role");
        
        if (role == null || !role.equals("ADMIN")) {
            throw new UnauthorizedAccessException(
                "This action requires admin privileges"
            );
        }
    }
    
    /**
     * Check if the authenticated user owns the resource
     */
    public void requireOwnership(HttpServletRequest request, String resourceOwnerId) {
        String userId = (String) request.getAttribute("userId");
        
        if (!userId.equals(resourceOwnerId)) {
            throw new UnauthorizedAccessException(
                "You don't have permission to access this resource"
            );
        }
    }
    
    /**
     * Check if user is admin OR owns the resource
     */
    public void requireAdminOrOwnership(
        HttpServletRequest request, 
        String resourceOwnerId
    ) {
        String userId = (String) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        
        boolean isAdmin = "ADMIN".equals(role);
        boolean isOwner = userId.equals(resourceOwnerId);
        
        if (!isAdmin && !isOwner) {
            throw new UnauthorizedAccessException(
                "You don't have permission to access this resource"
            );
        }
    }
    
    /**
     * Get the authenticated user's ID
     */
    public String getUserId(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        if (userId == null) {
            throw new UnauthorizedAccessException("Authentication required");
        }
        return userId;
    }
    
    /**
     * Get the authenticated user's role
     */
    public String getRole(HttpServletRequest request) {
        return (String) request.getAttribute("role");
    }
}