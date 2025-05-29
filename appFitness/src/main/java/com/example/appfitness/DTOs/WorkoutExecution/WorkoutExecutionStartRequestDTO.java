package com.example.appfitness.DTOs.WorkoutExecution;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExecutionStartRequestDTO {
    private Integer userId;
    private Integer workoutPlanId;
}
