package com.example.appfitness.services;

import com.example.appfitness.models.User;
import com.example.appfitness.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean emailExists(String email) {
        Integer result = userRepository.checkEmail(email);
        return result != null && result == 1;
    }

    public Optional<String> getEmailFromUserId(int id) {
        return userRepository.findEmailById(String.valueOf(id));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        // In production, use BCrypt or similar! For demo: plain comparison
        return rawPassword.equals(encodedPassword);
    }

    public User saveUser(User user) {
        // Encrypt password here in production!
        return userRepository.save(user);
    }
}
