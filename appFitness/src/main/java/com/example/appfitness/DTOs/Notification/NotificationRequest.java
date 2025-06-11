package com.example.appfitness.DTOs.Notification;

import com.example.appfitness.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private Integer receiverId;
    private String message;
    private Notification.NotificationType type;
}
