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

    private Integer repsPerformed;
    private Double weightPerformed;
    private Duration resTimePerformed; // secalhar nao vale a pena

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseExecution_id", nullable = false)
    private ExerciseExecution exerciseExecution;
}
