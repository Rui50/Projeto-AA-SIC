package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ExerciseData")
public class ExerciseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // o exercicio a que se refere
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    @ToString.Exclude
    private Exercise exercise;

    @OneToMany(mappedBy = "exerciseData", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @ToString.Exclude
    private List<SetData> plannedSets = new ArrayList<>();

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workoutPlan_id", nullable = false)
    @ToString.Exclude
    private WorkoutPlan workoutPlan;

    private boolean isDeleted = false;

    // é preciso aqui?

    public void addPlannedSet(SetData setData) {
        if (this.plannedSets == null) {
            this.plannedSets = new ArrayList<>();
        }
        this.plannedSets.add(setData);
        setData.setExerciseData(this);
    }

    public void removePlannedSet(SetData setData) {
        if (this.plannedSets != null) {
            this.plannedSets.remove(setData);
            setData.setExerciseData(null);
        }
    }

}
