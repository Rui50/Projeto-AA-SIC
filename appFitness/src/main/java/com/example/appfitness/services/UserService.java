package com.example.appfitness.services;

import com.example.appfitness.models.User;
import com.example.appfitness.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<String> findEmailById(int id) {
        return userRepository.findEmailById(String.valueOf(id));
    }

    // para verificar se um email ja existe, util para reg
    public boolean checkEmail(String email) {
        Integer result;
        result = userRepository.checkEmail(email);
        return result == 1;
    }


    public User saveUser(User user) {
        // falta cenas da password encrypt
        return userRepository.save(user);
    }


}
