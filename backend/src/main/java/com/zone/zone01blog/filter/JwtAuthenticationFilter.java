package com.zone.zone01blog.filter;

import com.zone.zone01blog.entity.User;
import com.zone.zone01blog.repository.UserRepository;
import com.zone.zone01blog.security.JwtAuthenticationToken;
import com.zone.zone01blog.util.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserRepository userRepository) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/api/v1/auth/") || path.startsWith("/swagger-ui/") || path.startsWith("/v3/api-docs")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(401, "Missing or invalid Authorization header");
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            response.sendError(401, "Invalid or expired token");
            return;
        }

        String userId = jwtUtil.getUserIdFromToken(token);
        String role = jwtUtil.getRoleFromToken(token);

        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent() && userOpt.get().isBanned()) {
            response.sendError(403, "Your account has been banned");
            return;
        }

        // Create authentication and store in SecurityContext
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(userId, role);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Continue go go go
        filterChain.doFilter(request, response);
    }
}