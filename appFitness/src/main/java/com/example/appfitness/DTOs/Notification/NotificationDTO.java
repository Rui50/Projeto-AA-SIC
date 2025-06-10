package com.example.appfitness.DTOs.Notification;

import com.example.appfitness.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private int id;
    private String type;
    private String message;
    private LocalDateTime timestamp;
    private boolean isRead;

    public static NotificationDTO fromEntity(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setType(String.valueOf(notification.getType()));
        dto.setMessage(notification.getMessage());
        dto.setTimestamp(notification.getCreatedAt());
        dto.setRead(notification.isRead());

        return dto;
    }
}
