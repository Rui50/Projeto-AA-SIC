package com.example.appfitness.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component
public class AuthMiddleware {
    private AuthService authService;

    private static final String MESSAGE_FIELD = "message";

    private AuthMiddleware(AuthService authService) {
        this.authService = authService;
    }

    public ResponseEntity<Object> checkTokenSimple(String token, Function<String, ResponseEntity<Object>> callback) {
        try {
            if(token == null || token.isEmpty() || !authService.checkTokenLight(token)){
                Map<String, String> error = Map.of(MESSAGE_FIELD, "Invalid token: \"" + token + "\"!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }
            return callback.apply(authService.getUserIdFromToken(token));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = Map.of(MESSAGE_FIELD, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }

    }
}
