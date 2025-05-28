package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exercise_executions")
public class ExerciseExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // relacionar ao exercicio que foi executado
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="exercise_data_id", nullable = false)
    private ExerciseData exerciseData;

    // relacionar com o workoutexecution
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_execution_id", nullable = false)
    private WorkoutExecution workoutExecution;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @OneToMany(mappedBy = "exerciseExecution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SetExecution> performedSets = new ArrayList<>();
}
