package com.example.appfitness.auth;
/*
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.appfitness.exceptions.authentication.UnauthorizedException;
import com.example.appfitness.models.User;
import com.example.appfitness.services.UserService;

import jakarta.servlet.http.Cookie;

import java.util.Optional;

@Service
public class AuthService {
    private final JwtService jwtService;
    private final UserService userService;

    @Value("${app.domain}")
    private String appDomain;

    public AuthService(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }


    public String login(User user) throws UnauthorizedException {
        if (user == null) throw new UnauthorizedException("User is null");
        Optional<User> userInDBOpt = userService.getUserByEmail(user.getEmail());
        if (userInDBOpt.isEmpty()) throw new UnauthorizedException("User not found");
        User userInDB = userInDBOpt.get();
        if (!userService.checkPassword(user.getPassword(), userInDB.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }
        return jwtService.generateToken(userInDB);
    }

    public String login(String email, String password) throws UnauthorizedException {
        if (email == null || password == null) throw new UnauthorizedException("Email or password is null");
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        return login(user);
    }


    public User register(User user) throws UnauthorizedException {
        String email = user.getEmail();
        if (userService.emailExists(email)) {
            throw new UnauthorizedException("User with email '" + user.getEmail() + "' already exists!");
        }
        return userService.saveUser(user);
    }

    public boolean checkUserId(String token, String userId) {
        if (token == null || userId == null) return false;
        String id = jwtService.extractUserId(token);
        return userId.equals(id);
    }

    public boolean checkIfIdHasEmail(String token, String email) {
        if (token == null || email == null) throw new UnauthorizedException("Token or email is null");
        Optional<String> emailFromDatabase = userService.getEmailFromUserId(Integer.parseInt(jwtService.extractUserId(token)));
        return emailFromDatabase.isPresent() && emailFromDatabase.get().equals(email);
    }

    public boolean checkTokenLight(String token) {
        try {
            return token != null && jwtService.isTokenValid(token);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUserIdFromToken(String token) {
        return jwtService.extractUserId(token);
    }

    public String extractUserId(String token) {
        return jwtService.extractUserId(token);
    }

    public String extractName(String token) {
        return jwtService.extractName(token);
    }

    public Cookie generateCookie(String email, String password){
        String token = login(email, password);
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain(appDomain);
        cookie.setPath("/");
        cookie.setMaxAge(24 * 60 * 60); // 1 day
        return cookie;
    }

    public Cookie deleteCookie(){
        Cookie cookie = new Cookie("token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setDomain(appDomain);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        return cookie;
    }
}
*/