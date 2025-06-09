package com.example.appfitness.services;

import com.example.appfitness.DTOs.Settings.SettingsDTO;
import com.example.appfitness.models.User;
import com.example.appfitness.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository) {
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.userRepository = userRepository;
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

    public SettingsDTO updateSettings(SettingsDTO settings, User user) {
        User.MetricType metricType = settings.getMetricType();
        if (metricType != null) {
            user.setMetricType(metricType);
        }

        userRepository.save(user);
        return settings;
    }

    public Optional<String> getEmailFromUserId(int id) {
        return userRepository.findEmailById(String.valueOf(id));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return bCryptPasswordEncoder.matches(rawPassword, encodedPassword);
        //return rawPassword.equals(encodedPassword);
    }

    public User saveUser(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
