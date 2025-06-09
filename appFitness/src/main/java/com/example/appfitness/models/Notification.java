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
@Table(name = "Notification")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver", nullable = false)
    private User receiver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private String title;

    private String message;

    @Column(name = "is_read")
    private boolean isRead;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    // se usarmos
    public enum NotificationType {
        WORKOUT_CREATED,
        WORKOUT_UPDATE,
        PROFESSOR_ASSIGNED,
        PROFESSOR_UNASSIGNED,
        PROFESSOR_NOTIFY,
        SCHEDULED_WORKOUT
    }
}