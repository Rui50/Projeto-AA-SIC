package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.stat.internal.StatsHelper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    @JoinColumn(name="exerciseData_id", nullable = false)
    private ExerciseData exerciseData;

    // relacionar com o workoutexecution
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workoutExecution_id", nullable = false)
    private WorkoutExecution workoutExecution;

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "exerciseExecution", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<SetExecution> performedSets = new ArrayList<>();

    public enum Status {

    }
}
