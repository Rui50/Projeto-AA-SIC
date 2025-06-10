package com.example.appfitness.services;

import com.example.appfitness.DTOs.Notification.NotificationDTO;
import com.example.appfitness.models.Notification;
import com.example.appfitness.models.User;
import com.example.appfitness.repositories.NotificationRepository;
import com.example.appfitness.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private NotificationRepository notificationRepository;
    private UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotification(Integer sentToId, String message, Notification.NotificationType type) {
        return createNotification(sentToId, message, type, false);
    }

    @Transactional
    public Notification createNotification(Integer sentToId, String message, Notification.NotificationType type, boolean isRead) {
        User recipient = userRepository.findById(sentToId)
                .orElseThrow(() -> new RuntimeException("Recipient user not found " + sentToId));
        Notification notification = new Notification();
        notification.setReceiver(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(isRead);
        return notificationRepository.save(notification);
    }

    public List<NotificationDTO> findAllNotificationsForuser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByReceiverId(userId);
        return notifications.stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    public List<NotificationDTO> findUnreadNotificationsForuser(Integer userId) {
        List<Notification> notifications = notificationRepository.findByReceiverIdAndUnread(userId);
        return notifications.stream()
                .map(NotificationDTO::fromEntity)
                .toList();
    }

    @Transactional
    public void markNotificationAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found with ID: " + notificationId));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllNotificationsAsRead(Integer userId) {
        User ser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found " + userId));

        List<Notification> notifications = notificationRepository.findByReceiverIdAndUnread(userId);
        notifications.forEach(notification -> {notification.setRead(true);});
        notificationRepository.saveAll(notifications);
    }
}
