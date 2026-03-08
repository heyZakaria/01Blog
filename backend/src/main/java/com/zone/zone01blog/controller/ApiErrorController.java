package com.zone.zone01blog.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ApiErrorController implements ErrorController {

    @RequestMapping("${server.error.path:/error}")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Object statusObj = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = statusObj != null ? Integer.parseInt(statusObj.toString()) : 500;
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        String path = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        Map<String, Object> error = new HashMap<>();
        error.put("status", status.value());
        error.put("message", status == HttpStatus.NOT_FOUND ? "Route not found" : "An unexpected error occurred");
        error.put("path", path);
        error.put("timestamp", LocalDateTime.now());

        return ResponseEntity.status(status).body(error);
    }
}
