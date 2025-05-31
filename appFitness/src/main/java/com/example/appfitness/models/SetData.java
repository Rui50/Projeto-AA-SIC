package com.example.appfitness.models;

import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
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
@Table(name = "ExerciseDataSet")
public class SetData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer setNumber; // indica se é o primeiro set, segundo, etc

    private Integer repsPlanned;
    private Double weightPlanned;
    private Float restTimeSugested;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exerciseData_id", nullable = false)
    private ExerciseData exerciseData;
}
