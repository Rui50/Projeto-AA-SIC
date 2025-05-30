package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "User")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String email;

    private String name;
    private String password;


    // metrica preferida
    @Enumerated(EnumType.STRING)
    private MetricType metricType;

    // every user will have objects BodyMetrics so we can track progress
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BodyMetrics> bodyMetrics = new ArrayList<>();

    // Para os workoutplans do user
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutPlan> ownedWorkoutPlans = new ArrayList<>();

    // Para as execuções de workouts que o user fez
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WorkoutExecution> workoutExecutions = new ArrayList<>();

    // notificações
    @OneToMany(mappedBy = "receiver", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Notification> notifications = new ArrayList<>();

    private void addBodyMetrics(BodyMetrics bodyMetrics) {
        this.bodyMetrics.add(bodyMetrics);
    }

    public enum MetricType {
        METRIC,
        IMPERIAL
    }
}
