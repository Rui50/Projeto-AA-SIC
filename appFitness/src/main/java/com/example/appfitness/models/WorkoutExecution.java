package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "WorkoutExecution")
public class WorkoutExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    @ToString.Exclude
    private WorkoutPlan workoutPlan;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate executionDate;

    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;

    private String feeback;

    // exercicios executados
    @OneToMany(mappedBy = "workoutExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ExerciseExecution> exerciseExecutions = new ArrayList<>();


    public void addExerciseExecution(ExerciseExecution execution) {
        exerciseExecutions.add(execution);
        execution.setWorkoutExecution(this);
    }

    public void removeExerciseExecution(ExerciseExecution execution) {
        exerciseExecutions.remove(execution);
        execution.setWorkoutExecution(null);
    }

    public enum WorkoutStatus {
        IN_PROGRESS, // para se quisermos manter no menu inicial que está a decorrer
        COMPLETED,
        SKIPPED,
        CANCELLED// necessário?
    }
}
