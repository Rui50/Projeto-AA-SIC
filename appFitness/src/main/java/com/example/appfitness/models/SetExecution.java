package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ExerciseExecutionSet")
public class SetExecution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer setNumber;

    private Integer repsPerformed;
    private Double weightPerformed;
    private Duration resTimePerformed; // secalhar nao vale a pena
    private boolean isCompleted; // because of newlly added
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_data_id")
    @ToString.Exclude
    private SetData plannedSet;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseExecution_id", nullable = false)
    @ToString.Exclude
    private ExerciseExecution exerciseExecution;
}
