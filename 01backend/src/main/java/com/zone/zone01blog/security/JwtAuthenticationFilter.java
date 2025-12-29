package com.zone.zone01blog.security;

import com.zone.zone01blog.util.JwtUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path = httpRequest.getRequestURI();
        
        // 1. Skip authentication for public endpoints
        if (isPublicPath(path)) {
            chain.doFilter(request, response);  // Continue without authentication
            return;
        }
        
        // 2. Extract token from Authorization header
        String authHeader = httpRequest.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorizedError(httpResponse, "Missing or invalid Authorization header");
            return;
        }
        
        String token = authHeader.substring(7);  // Remove "Bearer " prefix
        
        // 3. Validate token
        if (!jwtUtil.validateToken(token)) {
            sendUnauthorizedError(httpResponse, "Invalid or expired token");
            return;
        }
        
        // 4. Extract user info and store in request attributes
        String userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);
        
        httpRequest.setAttribute("userId", userId);
        httpRequest.setAttribute("role", role);
        // 5. Continue to controller
        chain.doFilter(request, response);
    }
    
    private boolean isPublicPath(String path) {
        return path.startsWith("/api/v1/auth/");
    }
    
    private void sendUnauthorizedError(HttpServletResponse response, String message) 
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}");
    }
}

/*  

[org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@46320c9a,
     org.springframework.security.web.context.SecurityContextPersistenceFilter@4d98e41b, 
     org.springframework.security.web.header.HeaderWriterFilter@52bd9a27,
      org.springframework.security.web.csrf.CsrfFilter@51c65a43,
       org.springframework.security.web.authentication.logout.LogoutFilter@124d26ba,
        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter@61e86192, 
        org.springframework.security.web.authentication.ui.DefaultLoginPageGeneratingFilter@10980560, 
        org.springframework.security.web.authentication.ui.DefaultLogoutPageGeneratingFilter@32256e68, 
        org.springframework.security.web.authentication.www.BasicAuthenticationFilter@52d0f583, 
        org.springframework.security.web.savedrequest.RequestCacheAwareFilter@5696c927,
         org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@5f025000, 
         org.springframework.security.web.authentication.AnonymousAuthenticationFilter@5e7abaf7,
          org.springframework.security.web.session.SessionManagementFilter@681c0ae6,
           org.springframework.security.web.access.ExceptionTranslationFilter@15639d09,
            org.springframework.security.web.access.intercept.FilterSecurityInterceptor@4f7be6c8]|
*/