package com.example.appfitness.DTOs.WorkoutPlan;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

// used to add or update details about a workout plan, including ex and its sets
@Getter
@Setter
@ToString
public class ExerciseDataRequestDTO {
    private Integer id;
    private Integer exerciseId; // id of exercise being added
    private String note;
    private List<SetDataDTO> plannedSets;
    /*
    @Override
    public String toString() {
        return "ExerciseDataRequestDTO{" +
                "exerciseId=" + exerciseId +
                ", note='" + note + '\'' +
                ", plannedSets=" + plannedSets +
                '}';
    }*/

}
