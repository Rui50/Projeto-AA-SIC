package com.example.appfitness.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "workout_exercise_data")
public class ExerciseData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // o exercicio a que se refere
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercise_id", nullable = false)
    private Exercise exercise;

    @OneToMany(mappedBy = "exerciseData", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SetData> plannedSets = new ArrayList<>();

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workoutPlan_id", nullable = false)
    private WorkoutPlan workoutPlan;

    // é preciso aqui?

    public void addPlannedSet(SetData setData){
        plannedSets.add(setData);
        setData.setExerciseData(this);
    }

    public void removePlannedSet(SetData setData){
        plannedSets.remove(setData);
        setData.setExerciseData(null);
    }

}
