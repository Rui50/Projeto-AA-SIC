package com.example.appfitness.controllers;

import com.example.appfitness.DTOs.Notification.NotificationDTO;
import com.example.appfitness.DTOs.Notification.NotificationRequest;
import com.example.appfitness.auth.AuthService;
import com.example.appfitness.models.Notification;
import com.example.appfitness.models.User;
import com.example.appfitness.services.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private NotificationService notificationService;
    private AuthService authService;

    public NotificationController(NotificationService notificationService, AuthService authService) {
        this.notificationService = notificationService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<NotificationDTO> createNotification(
            @RequestBody NotificationRequest request
    ) {
        Notification createdNotification = notificationService.createNotification(
                request.getReceiverId(),
                request.getMessage(),
                request.getType()
        );
        return new ResponseEntity<>(NotificationDTO.fromEntity(createdNotification), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<NotificationDTO>> getUnreadNotifications(
            @CookieValue(value = "token", defaultValue = "") String token
    ) {
        int userId = Integer.parseInt(authService.getUserIdFromToken(token));

        List<NotificationDTO> notifications = notificationService.findUnreadNotificationsForuser(userId);;
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/all")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(
            @CookieValue(value = "token", defaultValue = "") String token
    ) {
        int userId = Integer.parseInt(authService.getUserIdFromToken(token));

        List<NotificationDTO> notifications = notificationService.findAllNotificationsForuser(userId);;
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}")
    public ResponseEntity<List<Notification>> markNotificationAsRead(
            @PathVariable Integer notificationId,
            @CookieValue(value = "token", defaultValue = "") String token
    ) {
        int userId = Integer.parseInt(authService.getUserIdFromToken(token));
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/markAll")
    public ResponseEntity<Void> markAllNotificationsAsRead(
            @CookieValue(value = "token", defaultValue = "") String token
    ) {
        int userId = Integer.parseInt(authService.getUserIdFromToken(token));
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.noContent().build();
    }


}
