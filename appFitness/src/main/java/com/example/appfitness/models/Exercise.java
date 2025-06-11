package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Exercise")
public class Exercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private String name;    
    private String description;

    @Enumerated(EnumType.STRING)
    private ExerciseType type;
    @Enumerated(EnumType.STRING)
    private MuscleGroup muscleGroup;

    // trabalho futuro
    private String videoURL;
    private String imageURL;

    public enum ExerciseType {
        STRENGTH,
        CARDIO,
        BODYWEIGHT
    }

    public enum MuscleGroup {
        CHEST,
        BACK,
        LEGS,
        SHOULDERS,
        ARMS,
        CORE
    }
}


