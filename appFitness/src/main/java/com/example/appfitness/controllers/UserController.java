package com.example.appfitness.controllers;

import com.example.appfitness.models.User;
import com.example.appfitness.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /*@GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable int id) {

    }*/

    /*@GetMapping("/all")
    public ResponseEntity<List<Object>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        // falta o DTO para devolver
    }*/


}
