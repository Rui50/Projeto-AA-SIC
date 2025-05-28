package com.example.appfitness.models;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String message;

    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    // se usarmos
    public enum NotificationType {
        WORKOUT_REMINDER,
        WORKOUT_UPDATE,
        PROFESSOR_REQUEST_RECEIVED,
        PROFESSOR_REQUEST_STATUS,
        GENERAL_ANNOUNCEMENT
    }
}