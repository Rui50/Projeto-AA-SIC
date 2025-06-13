package com.example.appfitness.controllers;

import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.Aluno;
import com.example.appfitness.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    @Value("${app.domain}")
    private String appDomain;

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

            // Set cookie manually so SameSite is applied
            String cookieValue = String.format(
                "token=%s; Path=/; HttpOnly; Secure; SameSite=None; Domain=%s",
                token, appDomain // Make sure appDomain does NOT have protocol or trailing /
            );
            response.addHeader("Set-Cookie", cookieValue);

            String name = authService.extractName(token);
            String email = authService.extractEmail(token);

            Map<String, String> responseData = new HashMap<>();
            responseData.put("token", token);
            responseData.put("role", role);
            responseData.put("userId", authService.getUserIdFromToken(token));
            responseData.put("name", name);
            responseData.put("email", email);
            responseData.put("metricType", authService.extractMetricType(token));
            responseData.put("message", "Login successful! (" + user.getEmail() + ")");

            return ResponseEntity.ok().body(responseData);
        } catch (RuntimeException e) {
            Map<String, String> error = Map.of("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        } catch (Exception e) {
            Map<String, String> error = Map.of("message", Arrays.toString(e.getStackTrace()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Logout endpoint (deletes cookie)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        String cookieValue = String.format(
            "token=; Path=/; HttpOnly; Secure; SameSite=None; Domain=%s; Max-Age=0",
            appDomain
        );
        response.addHeader("Set-Cookie", cookieValue);
        return ResponseEntity.ok().body("Logged out successfully");
    }
}
