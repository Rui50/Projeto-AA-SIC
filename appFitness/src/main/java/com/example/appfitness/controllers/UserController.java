package com.example.appfitness.controllers;

import com.example.appfitness.auth.JwtService;
import com.example.appfitness.models.User;
import com.example.appfitness.services.UserService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final JwtService jwtService;
    private final UserService userService;

    public UserController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    // Get currently logged in user's info (by token)
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        String userIdStr = jwtService.extractUserId(token);

        int userId = Integer.parseInt(userIdStr);
        Optional<User> userOpt = userService.getUserById(userId);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        User user = userOpt.get();
        // Avoid sending password back!
        user.setPassword(null);
        return ResponseEntity.ok(user);
    }
}
