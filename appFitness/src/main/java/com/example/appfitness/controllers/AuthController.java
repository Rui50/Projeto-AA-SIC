package com.example.appfitness.controllers;

import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.User;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Register endpoint - only for students (professors added by admins)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Aluno aluno) {
        try {
            Aluno registeredAluno = authService.registerAluno(aluno);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        }
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user, HttpServletResponse response) {
        try {
            String token = authService.login(user);
            String role = authService.getRoleFromToken(token);
            Cookie cookie = authService.generateCookie(user.getEmail(), user.getPassword());
            String name = authService.extractName(token);
            String email = authService.extractEmail(token);
            response.addCookie(cookie);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("role", role);
            responseData.put("userId", authService.getUserIdFromToken(token));
            responseData.put("name", name);
            responseData.put("email", email);

            // talvez mais tarde, metricType

            return ResponseEntity.ok().body(responseData);//body("{\"token\": \"" + token + "\"}");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
    }

    // Logout endpoint (deletes cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = authService.deleteCookie();
        response.addCookie(cookie);
        return ResponseEntity.ok().body("Logged out successfully");
    }
}
