package com.example.appfitness.DTOs.WorkoutPlan;

import com.example.appfitness.models.WorkoutPlan;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutPlanSummary {
    private Integer id;
    private String name;

    public static WorkoutPlanSummary fromEntity(WorkoutPlan workoutPlan) {
        WorkoutPlanSummary dto = new WorkoutPlanSummary();
        dto.setId(workoutPlan.getId());
        dto.setName(workoutPlan.getName());
        return dto;
    }
}

