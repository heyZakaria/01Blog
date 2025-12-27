package com.zone.zone01blog.util;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class AuthContext {
    
    public String getCurrentUserId() {
        HttpServletRequest request = getCurrentRequest();
        return (String) request.getAttribute("userId");
    }
    
    public String getCurrentUserRole() {
        HttpServletRequest request = getCurrentRequest();
        return (String) request.getAttribute("role");
    }
    
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attrs = 
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            //
        return attrs.getRequest();
    }
}