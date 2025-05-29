package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "workout_executions")
public class WorkoutExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDate executionDate;

    @Enumerated(EnumType.STRING)
    private WorkoutStatus status;

    private String feeback;

    // exercicios executados
    @OneToMany(mappedBy = "workoutExecution", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ExerciseExecution> exerciseExecutions = new ArrayList<>();


    public void addExerciseExecution(ExerciseExecution execution) {
        exerciseExecutions.add(execution);
        execution.setWorkoutExecution(this);
    }

    public void removeExerciseExecution(ExerciseExecution execution) {
        exerciseExecutions.remove(execution);
        execution.setWorkoutExecution(null);
    }

    public

    public enum WorkoutStatus {
        IN_PROGRESS, // para se quisermos manter no menu inicial que está a decorrer
        COMPLETED,
        SKIPPED // necessário?
    }
}
