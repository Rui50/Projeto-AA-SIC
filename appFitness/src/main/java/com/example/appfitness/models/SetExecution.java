package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "set_executions")
public class SetExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer setNumber;

    private Integer actualReps;
    private Double actualWeight;
    private Duration actualRestTime; // secalhar nao vale a pena

    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_execution_id", nullable = false)
    private ExerciseExecution exerciseExecution;
}
