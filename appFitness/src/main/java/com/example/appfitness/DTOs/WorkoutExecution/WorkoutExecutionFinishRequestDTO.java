package com.example.appfitness.DTOs.WorkoutExecution;

import com.example.appfitness.models.WorkoutExecution;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutExecutionFinishRequestDTO {
    private String feedback;
    private WorkoutExecution.WorkoutStatus status; // frontend returns state?
}
