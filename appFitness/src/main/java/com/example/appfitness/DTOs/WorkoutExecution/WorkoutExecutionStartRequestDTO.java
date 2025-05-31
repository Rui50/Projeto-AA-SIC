package com.example.appfitness.DTOs.WorkoutExecution;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class WorkoutExecutionStartRequestDTO {
    private Integer userId;
    private Integer workoutPlanId;
}
