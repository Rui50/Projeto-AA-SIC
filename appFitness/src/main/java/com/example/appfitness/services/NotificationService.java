package com.example.appfitness.services;

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
        User recipient = userRepository.findById(sentToId)
                .orElseThrow(() -> new RuntimeException("Recipient user not found " + sentToId));
        Notification notification = new Notification();
        notification.setReceiver(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRead(false);
        return notificationRepository.save(notification);
    }

    public List<Notification> findUserNotifications(Integer receiverId) {
        return notificationRepository.findByReceiverId(receiverId);
    }
    public List<Notification> findUnreadNotificationsForuser(Integer userId) {
        return notificationRepository.findByReceiverIdAndUnread(userId);
    }

    @Transactional
    public Notification markNotificationAsRead(Integer id) {
        return notificationRepository.findById(id).map(notification -> {
            notification.setRead(true);
            return notificationRepository.save(notification);
        }).orElseThrow(() -> new RuntimeException("Notification not found " + id));
    }
}
